package com.example.campusfix.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onIrParaRegisto: () -> Unit
) {
    val loading by viewModel.loading.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var erroEmail by remember { mutableStateOf<Int?>(null) }
    var erroPassword by remember { mutableStateOf<Int?>(null) }

    fun validar(): Boolean {
        erroEmail = when {
            email.isBlank() -> R.string.erro_campo_obrigatorio
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.erro_email_invalido
            else -> null
        }
        erroPassword = when {
            password.isBlank() -> R.string.erro_campo_obrigatorio
            else -> null
        }
        return erroEmail == null && erroPassword == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_campusfix),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.app_slogan), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; erroEmail = null; viewModel.limparErro() },
            label = { Text(stringResource(R.string.email)) },
            isError = erroEmail != null,
            supportingText = { erroEmail?.let { Text(stringResource(it)) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; erroPassword = null; viewModel.limparErro() },
            label = { Text(stringResource(R.string.password)) },
            isError = erroPassword != null,
            supportingText = { erroPassword?.let { Text(stringResource(it)) } },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (erro != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.erro_autenticacao),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { if (validar()) viewModel.login(email.trim(), password) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.btn_entrar))
            }
        }
        TextButton(onClick = onIrParaRegisto) {
            Text(stringResource(R.string.sem_conta_registar))
        }
    }
}
