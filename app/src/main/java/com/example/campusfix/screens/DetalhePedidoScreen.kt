package com.example.campusfix.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campusfix.R
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhePedidoScreen(
    pedidoId: Long,
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    onVoltar: () -> Unit
) {
    val perfil by authViewModel.perfil.collectAsState()
    val pedido by pedidosViewModel.pedidoAtual.collectAsState()
    val isAdmin = perfil?.isAdmin == true

    var mostrarConfirmacao by remember { mutableStateOf(false) }

    LaunchedEffect(pedidoId) {
        pedidosViewModel.carregarDetalhe(pedidoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalhe_pedido)) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                }
            )
        }
    ) { padding ->
        val p = pedido
        if (p == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CampoDetalhe(R.string.categoria, p.categorias?.nome ?: "-")
            CampoDetalhe(R.string.localizacao, p.localizacao)
            CampoDetalhe(R.string.descricao, p.descricao)
            CampoDetalhe(R.string.data_criacao, p.dataFormatada)
            CampoDetalhe(R.string.estado, stringResource(p.estadoEnum.label))
            if (isAdmin && p.profiles != null) {
                CampoDetalhe(R.string.criado_por, p.profiles.nome)
            }

            p.fotoUrl?.let { url ->
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.fotografia),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(4.dp))
                AsyncImage(
                    model = url,
                    contentDescription = stringResource(R.string.fotografia),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            if (isAdmin) {
                Text(
                    stringResource(R.string.alterar_estado),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    EstadoPedido.entries.forEach { estado ->
                        FilterChip(
                            selected = p.estado == estado.db,
                            onClick = { pedidosViewModel.alterarEstado(pedidoId, estado) },
                            label = { Text(stringResource(estado.label)) }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { mostrarConfirmacao = true },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_eliminar_pedido))
                }
            } else if (p.estadoEnum != EstadoPedido.CONCLUIDO) {
                // O utilizador pode cancelar pedidos ainda não concluídos
                OutlinedButton(
                    onClick = { mostrarConfirmacao = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_cancelar_pedido))
                }
            }
        }

        if (mostrarConfirmacao) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacao = false },
                title = {
                    Text(
                        stringResource(
                            if (isAdmin) R.string.confirmar_eliminar_pedido
                            else R.string.confirmar_cancelar_pedido
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarConfirmacao = false
                        pedidosViewModel.eliminarPedido(pedidoId, onSucesso = onVoltar)
                    }) {
                        Text(stringResource(R.string.confirmar))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacao = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }
    }
}

@Composable
private fun CampoDetalhe(labelRes: Int, valor: String) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(
            stringResource(labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
