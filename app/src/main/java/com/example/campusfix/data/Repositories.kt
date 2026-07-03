package com.example.campusfix.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

object AuthRepository {

    private val client = SupabaseModule.client

    val sessionStatus get() = client.auth.sessionStatus

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    suspend fun login(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(nome: String, email: String, password: String, tipoPerfil: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("nome", nome)
                put("tipo_perfil", tipoPerfil)
            }
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }

    /**
     * Garante que existe uma linha na tabela profiles para o utilizador autenticado.
     * Os dados vêm dos metadados definidos no registo.
     */
    suspend fun carregarPerfil(): Perfil? {
        val user = client.auth.currentUserOrNull() ?: return null
        val existente = client.from("profiles")
            .select { filter { eq("id", user.id) } }
            .decodeSingleOrNull<Perfil>()
        if (existente != null) return existente

        val meta = user.userMetadata
        val perfil = Perfil(
            id = user.id,
            nome = meta?.get("nome")?.jsonPrimitive?.content ?: "",
            email = user.email ?: "",
            tipoPerfil = meta?.get("tipo_perfil")?.jsonPrimitive?.content ?: "utilizador"
        )
        client.from("profiles").upsert(perfil)
        return perfil
    }

    suspend fun atualizarPerfil(nome: String, email: String, novaPassword: String?): Perfil? {
        val userId = currentUserId() ?: return null
        client.auth.updateUser {
            this.email = email
            if (!novaPassword.isNullOrBlank()) this.password = novaPassword
            data = buildJsonObject { put("nome", nome) }
        }
        client.from("profiles").update({
            set("nome", nome)
            set("email", email)
        }) {
            filter { eq("id", userId) }
        }
        return carregarPerfil()
    }
}

object PedidosRepository {

    private val client = SupabaseModule.client
    private val colunas = Columns.raw("*, categorias(nome), profiles(nome)")

    suspend fun listarTodos(): List<Pedido> =
        client.from("pedidos")
            .select(colunas) { order("created_at", Order.DESCENDING) }
            .decodeList()

    suspend fun listarDoUtilizador(userId: String): List<Pedido> =
        client.from("pedidos")
            .select(colunas) {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()

    suspend fun obter(id: Long): Pedido? =
        client.from("pedidos")
            .select(colunas) { filter { eq("id", id) } }
            .decodeSingleOrNull()

    suspend fun criar(pedido: NovoPedido) {
        client.from("pedidos").insert(pedido)
    }

    suspend fun alterarEstado(id: Long, estado: EstadoPedido) {
        client.from("pedidos").update({
            set("estado", estado.db)
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun eliminar(id: Long) {
        client.from("pedidos").delete { filter { eq("id", id) } }
    }

    /** Faz upload da fotografia para o bucket "fotos" e devolve o URL público. */
    suspend fun uploadFoto(bytes: ByteArray): String {
        val caminho = "${UUID.randomUUID()}.jpg"
        client.storage.from("fotos").upload(caminho, bytes)
        return client.storage.from("fotos").publicUrl(caminho)
    }
}

object CategoriasRepository {

    private val client = SupabaseModule.client

    suspend fun listar(): List<Categoria> =
        client.from("categorias")
            .select { order("nome", Order.ASCENDING) }
            .decodeList()

    suspend fun criar(nome: String) {
        client.from("categorias").insert(Categoria(nome = nome))
    }

    suspend fun editar(id: Long, nome: String) {
        client.from("categorias").update({
            set("nome", nome)
        }) {
            filter { eq("id", id) }
        }
    }

    suspend fun eliminar(id: Long) {
        client.from("categorias").delete { filter { eq("id", id) } }
    }
}
