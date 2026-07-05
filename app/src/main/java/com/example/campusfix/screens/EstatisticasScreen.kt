package com.example.campusfix.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

@Composable
fun EstatisticasScreen(
    pedidosViewModel: PedidosViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val perfil by authViewModel.perfil.collectAsState()
    val pedidos by pedidosViewModel.pedidos.collectAsState()

    LaunchedEffect(perfil) {
        perfil?.let { pedidosViewModel.carregar(it.isAdmin, it.id) }
    }

    val total = pedidos.size

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.estatisticas_titulo), style = MaterialTheme.typography.titleLarge)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.total_pedidos), style = MaterialTheme.typography.labelLarge)
                Text("$total", style = MaterialTheme.typography.displaySmall)
            }
        }

        EstadoPedido.entries.forEach { estado ->
            val quantidade = pedidos.count { it.estado == estado.db }
            val fracao = if (total > 0) quantidade.toFloat() / total else 0f
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        Text(
                            stringResource(estado.label),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text("$quantidade", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { fracao },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }
        }
    }
}
