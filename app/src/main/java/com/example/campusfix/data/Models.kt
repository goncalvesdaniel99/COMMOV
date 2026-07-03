package com.example.campusfix.data

import androidx.annotation.StringRes
import com.example.campusfix.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EstadoPedido(val db: String, @StringRes val label: Int) {
    SUBMETIDO("submetido", R.string.estado_submetido),
    EM_ANALISE("em_analise", R.string.estado_em_analise),
    CONCLUIDO("concluido", R.string.estado_concluido),
    REJEITADO("rejeitado", R.string.estado_rejeitado);

    companion object {
        fun fromDb(valor: String?): EstadoPedido =
            entries.firstOrNull { it.db == valor } ?: SUBMETIDO
    }
}

@Serializable
data class Perfil(
    val id: String,
    val nome: String = "",
    val email: String = "",
    @SerialName("tipo_perfil") val tipoPerfil: String = "utilizador"
) {
    val isAdmin: Boolean get() = tipoPerfil == "admin"
}

@Serializable
data class Categoria(
    val id: Long? = null,
    val nome: String = ""
)

@Serializable
data class CategoriaRef(val nome: String = "")

@Serializable
data class PerfilRef(val nome: String = "")

@Serializable
data class Pedido(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("categoria_id") val categoriaId: Long? = null,
    val localizacao: String = "",
    val descricao: String = "",
    val estado: String = EstadoPedido.SUBMETIDO.db,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val categorias: CategoriaRef? = null,
    val profiles: PerfilRef? = null
) {
    val estadoEnum: EstadoPedido get() = EstadoPedido.fromDb(estado)

    val dataFormatada: String
        get() = createdAt
            ?.take(16)
            ?.replace('T', ' ')
            ?: ""
}

@Serializable
data class NovoPedido(
    @SerialName("user_id") val userId: String,
    @SerialName("categoria_id") val categoriaId: Long,
    val localizacao: String,
    val descricao: String,
    val estado: String = EstadoPedido.SUBMETIDO.db,
    @SerialName("foto_url") val fotoUrl: String? = null
)
