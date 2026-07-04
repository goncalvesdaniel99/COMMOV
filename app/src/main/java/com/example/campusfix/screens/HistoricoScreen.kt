package com.example.campusfix.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

@Composable
fun HistoricoScreen(
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    onAbrirPedido: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val perfil by authViewModel.perfil.collectAsState()
    val pedidos by pedidosViewModel.pedidos.collectAsState()

    LaunchedEffect(perfil) {
        perfil?.let { pedidosViewModel.carregar(it.isAdmin, it.id) }
    }

    val concluidos = pedidos.filter { it.estado == EstadoPedido.CONCLUIDO.db }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.historico_titulo),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))
        if (concluidos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.sem_pedidos))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(concluidos, key = { it.id ?: 0L }) { pedido ->
                    PedidoCard(
                        pedido = pedido,
                        mostrarAutor = false,
                        onClick = { pedido.id?.let(onAbrirPedido) }
                    )
                }
            }
        }
    }
}
