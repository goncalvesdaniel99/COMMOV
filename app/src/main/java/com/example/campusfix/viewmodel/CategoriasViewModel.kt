package com.example.campusfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusfix.data.Categoria
import com.example.campusfix.data.CategoriasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun carregar() {
        viewModelScope.launch {
            try {
                _categorias.value = CategoriasRepository.listar()
            } catch (e: Exception) {
                _erro.value = "carregar"
            }
        }
    }

    fun criar(nome: String) {
        viewModelScope.launch {
            try {
                CategoriasRepository.criar(nome)
                carregar()
            } catch (e: Exception) {
                _erro.value = "guardar"
            }
        }
    }

    fun editar(id: Long, nome: String) {
        viewModelScope.launch {
            try {
                CategoriasRepository.editar(id, nome)
                carregar()
            } catch (e: Exception) {
                _erro.value = "guardar"
            }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            try {
                CategoriasRepository.eliminar(id)
                carregar()
            } catch (e: Exception) {
                _erro.value = "guardar"
            }
        }
    }

    fun limparErro() {
        _erro.value = null
    }
}
