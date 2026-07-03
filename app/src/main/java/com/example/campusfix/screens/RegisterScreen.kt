package com.example.campusfix.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onIrParaLogin: () -> Unit
) {
    val loading by viewModel.loading.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmacao by remember { mutableStateOf("") }
    var tipoPerfil by remember { mutableStateOf("utilizador") }

    var erroNome by remember { mutableStateOf<Int?>(null) }
    var erroEmail by remember { mutableStateOf<Int?>(null) }
    var erroPassword by remember { mutableStateOf<Int?>(null) }
    var erroConfirmacao by remember { mutableStateOf<Int?>(null) }

    fun validar(): Boolean {
        erroNome = if (nome.isBlank()) R.string.erro_campo_obrigatorio else null
        erroEmail = when {
            email.isBlank() -> R.string.erro_campo_obrigatorio
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.erro_email_invalido
            else -> null
        }
        erroPassword = when {
            password.isBlank() -> R.string.erro_campo_obrigatorio
            password.length < 6 -> R.string.erro_password_curta
            else -> null
        }
        erroConfirmacao = when {
            confirmacao != password -> R.string.erro_passwords_diferentes
            else -> null
        }
        return listOf(erroNome, erroEmail, erroPassword, erroConfirmacao).all { it == null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

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
            value = password,
            onValueChange = { password = it; erroPassword = null },
            label = { Text(stringResource(R.string.password)) },
            isError = erroPassword != null,
            supportingText = { erroPassword?.let { Text(stringResource(it)) } },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = confirmacao,
            onValueChange = { confirmacao = it; erroConfirmacao = null },
            label = { Text(stringResource(R.string.password_confirm)) },
            isError = erroConfirmacao != null,
            supportingText = { erroConfirmacao?.let { Text(stringResource(it)) } },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.tipo_perfil),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = tipoPerfil == "utilizador",
                    onClick = { tipoPerfil = "utilizador" }
                )
            ) {
                RadioButton(
                    selected = tipoPerfil == "utilizador",
                    onClick = { tipoPerfil = "utilizador" }
                )
                Text(stringResource(R.string.perfil_utilizador))
            }
            Spacer(Modifier.size(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = tipoPerfil == "admin",
                    onClick = { tipoPerfil = "admin" }
                )
            ) {
                RadioButton(
                    selected = tipoPerfil == "admin",
                    onClick = { tipoPerfil = "admin" }
                )
                Text(stringResource(R.string.perfil_admin))
            }
        }

        if (erro != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.erro_registo),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (validar()) {
                    viewModel.registar(nome.trim(), email.trim(), password, tipoPerfil)
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.btn_registar))
            }
        }
        TextButton(onClick = onIrParaLogin) {
            Text(stringResource(R.string.ja_tem_conta))
        }
    }
}
