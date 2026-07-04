package com.example.campusfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.data.NovoPedido
import com.example.campusfix.data.Pedido
import com.example.campusfix.data.PedidosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidosViewModel : ViewModel() {

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _pedidoAtual = MutableStateFlow<Pedido?>(null)
    val pedidoAtual: StateFlow<Pedido?> = _pedidoAtual.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    // Filtros (requisitos de valorização)
    val pesquisa = MutableStateFlow("")
    val filtroEstado = MutableStateFlow<EstadoPedido?>(null)
    val ordenarDescendente = MutableStateFlow(true)

    fun pedidosFiltrados(): List<Pedido> {
        var lista = _pedidos.value
        filtroEstado.value?.let { estado ->
            lista = lista.filter { it.estado == estado.db }
        }
        val texto = pesquisa.value.trim()
        if (texto.isNotEmpty()) {
            lista = lista.filter {
                it.descricao.contains(texto, ignoreCase = true) ||
                        it.localizacao.contains(texto, ignoreCase = true) ||
                        (it.categorias?.nome ?: "").contains(texto, ignoreCase = true)
            }
        }
        lista = if (ordenarDescendente.value) {
            lista.sortedByDescending { it.createdAt ?: "" }
        } else {
            lista.sortedBy { it.createdAt ?: "" }
        }
        return lista
    }

    fun carregar(isAdmin: Boolean, userId: String?) {
        viewModelScope.launch {
            _loading.value = true
            _erro.value = null
            try {
                _pedidos.value = if (isAdmin) {
                    PedidosRepository.listarTodos()
                } else if (userId != null) {
                    PedidosRepository.listarDoUtilizador(userId)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                _erro.value = "carregar"
            } finally {
                _loading.value = false
            }
        }
    }

    fun carregarDetalhe(id: Long) {
        viewModelScope.launch {
            _pedidoAtual.value = null
            try {
                _pedidoAtual.value = PedidosRepository.obter(id)
            } catch (e: Exception) {
                _erro.value = "carregar"
            }
        }
    }

    fun criarPedido(
        userId: String,
        categoriaId: Long,
        localizacao: String,
        descricao: String,
        fotoBytes: ByteArray?,
        onSucesso: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _erro.value = null
            try {
                val fotoUrl = fotoBytes?.let { PedidosRepository.uploadFoto(it) }
                PedidosRepository.criar(
                    NovoPedido(
                        userId = userId,
                        categoriaId = categoriaId,
                        localizacao = localizacao,
                        descricao = descricao,
                        fotoUrl = fotoUrl
                    )
                )
                onSucesso()
            } catch (e: Exception) {
                _erro.value = "guardar"
            } finally {
                _loading.value = false
            }
        }
    }

    fun alterarEstado(id: Long, estado: EstadoPedido) {
        viewModelScope.launch {
            _erro.value = null
            try {
                PedidosRepository.alterarEstado(id, estado)
                carregarDetalhe(id)
            } catch (e: Exception) {
                _erro.value = "guardar"
            }
        }
    }

    fun eliminarPedido(id: Long, onSucesso: () -> Unit) {
        viewModelScope.launch {
            _erro.value = null
            try {
                PedidosRepository.eliminar(id)
                onSucesso()
            } catch (e: Exception) {
                _erro.value = "guardar"
            }
        }
    }

    fun limparErro() {
        _erro.value = null
    }
}
