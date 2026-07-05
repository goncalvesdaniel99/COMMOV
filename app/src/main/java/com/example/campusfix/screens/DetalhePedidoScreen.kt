package com.example.campusfix.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campusfix.R
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

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

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        HeaderVoltar(stringResource(R.string.detalhe_pedido), onVoltar)

        val p = pedido
        if (p == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        EstadoPill(p.estadoEnum)
        Spacer(Modifier.height(12.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                CampoDetalhe(R.string.categoria, p.categorias?.nome ?: "-")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                CampoDetalhe(R.string.localizacao, p.localizacao)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                CampoDetalhe(R.string.descricao, p.descricao)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                CampoDetalhe(R.string.data_criacao, p.dataFormatada)
                if (isAdmin && p.profiles != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    CampoDetalhe(R.string.criado_por, p.profiles.nome)
                }
            }
        }

        p.fotoUrl?.let { url ->
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.fotografia),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = url,
                contentDescription = stringResource(R.string.fotografia),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.btn_eliminar_pedido))
            }
        } else if (p.estadoEnum != EstadoPedido.CONCLUIDO) {
            // O utilizador pode cancelar pedidos ainda não concluídos
            OutlinedButton(
                onClick = { mostrarConfirmacao = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.btn_cancelar_pedido))
            }
        }
        Spacer(Modifier.height(24.dp))
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

@Composable
private fun CampoDetalhe(labelRes: Int, valor: String) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(
            stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
