package com.example.campusfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusfix.data.AuthRepository
import com.example.campusfix.data.Perfil
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    val sessionStatus: StateFlow<SessionStatus> = AuthRepository.sessionStatus

    init {
        // Garante que o perfil é carregado também quando a sessão é restaurada no arranque
        viewModelScope.launch {
            sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated && _perfil.value == null) {
                    carregarPerfil()
                }
            }
        }
    }

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun limparErro() {
        _erro.value = null
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _erro.value = null
            try {
                AuthRepository.login(email, password)
                carregarPerfil()
            } catch (e: Exception) {
                _erro.value = "login"
            } finally {
                _loading.value = false
            }
        }
    }

    fun registar(nome: String, email: String, password: String, tipoPerfil: String) {
        viewModelScope.launch {
            _loading.value = true
            _erro.value = null
            try {
                AuthRepository.register(nome, email, password, tipoPerfil)
                carregarPerfil()
            } catch (e: Exception) {
                _erro.value = "registo"
            } finally {
                _loading.value = false
            }
        }
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            try {
                _perfil.value = AuthRepository.carregarPerfil()
            } catch (e: Exception) {
                _perfil.value = null
            }
        }
    }

    fun atualizarPerfil(nome: String, email: String, novaPassword: String?, onSucesso: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _erro.value = null
            try {
                _perfil.value = AuthRepository.atualizarPerfil(nome, email, novaPassword)
                onSucesso()
            } catch (e: Exception) {
                _erro.value = "guardar"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                AuthRepository.logout()
            } finally {
                _perfil.value = null
            }
        }
    }
}
