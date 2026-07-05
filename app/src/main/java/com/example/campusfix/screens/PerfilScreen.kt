package com.example.campusfix.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.ui.theme.ThemePrefs
import com.example.campusfix.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val perfil by authViewModel.perfil.collectAsState()
    val loading by authViewModel.loading.collectAsState()
    val erro by authViewModel.erro.collectAsState()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var novaPassword by remember { mutableStateOf("") }

    var erroNome by remember { mutableStateOf<Int?>(null) }
    var erroEmail by remember { mutableStateOf<Int?>(null) }
    var erroPassword by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(perfil) {
        perfil?.let {
            nome = it.nome
            email = it.email
        }
    }

    fun validar(): Boolean {
        erroNome = if (nome.isBlank()) R.string.erro_campo_obrigatorio else null
        erroEmail = when {
            email.isBlank() -> R.string.erro_campo_obrigatorio
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.erro_email_invalido
            else -> null
        }
        erroPassword = when {
            novaPassword.isNotEmpty() && novaPassword.length < 6 -> R.string.erro_password_curta
            else -> null
        }
        return listOf(erroNome, erroEmail, erroPassword).all { it == null }
    }

    val msgAtualizado = stringResource(R.string.perfil_atualizado)

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.editar_perfil), style = MaterialTheme.typography.titleLarge)
        perfil?.let {
            Text(
                stringResource(if (it.isAdmin) R.string.perfil_admin else R.string.perfil_utilizador),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it; erroNome = null },
            label = { Text(stringResource(R.string.nome)) },
            isError = erroNome != null,
            supportingText = { erroNome?.let { Text(stringResource(it)) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; erroEmail = null },
            label = { Text(stringResource(R.string.email)) },
            isError = erroEmail != null,
            supportingText = { erroEmail?.let { Text(stringResource(it)) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = novaPassword,
            onValueChange = { novaPassword = it; erroPassword = null },
            label = { Text(stringResource(R.string.nova_password_opcional)) },
            isError = erroPassword != null,
            supportingText = { erroPassword?.let { Text(stringResource(it)) } },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (erro != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.erro_guardar),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (validar()) {
                    authViewModel.atualizarPerfil(
                        nome.trim(),
                        email.trim(),
                        novaPassword.ifBlank { null }
                    ) {
                        Toast.makeText(context, msgAtualizado, Toast.LENGTH_SHORT).show()
                        novaPassword = ""
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.btn_guardar))
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        val sistemaEscuro = isSystemInDarkTheme()
        val escuro = ThemePrefs.modoEscuro.value ?: sistemaEscuro
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.modo_escuro), modifier = Modifier.weight(1f))
            Switch(
                checked = escuro,
                onCheckedChange = { ThemePrefs.guardar(context, it) }
            )
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { authViewModel.logout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.terminar_sessao))
        }
    }
}
