package com.example.campusfix.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.campusfix.data.Pedido
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

@Composable
fun PedidosListScreen(
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    onNovoPedido: (() -> Unit)?,
    onAbrirPedido: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val perfil by authViewModel.perfil.collectAsState()
    val loading by pedidosViewModel.loading.collectAsState()
    val erro by pedidosViewModel.erro.collectAsState()
    val pesquisa by pedidosViewModel.pesquisa.collectAsState()
    val filtroEstado by pedidosViewModel.filtroEstado.collectAsState()
    val ordenarDesc by pedidosViewModel.ordenarDescendente.collectAsState()
    // Recolhido apenas para recompor a lista quando os dados chegam
    val todos by pedidosViewModel.pedidos.collectAsState()

    LaunchedEffect(perfil) {
        perfil?.let { pedidosViewModel.carregar(it.isAdmin, it.id) }
    }

    val titulo = if (perfil?.isAdmin == true) {
        stringResource(R.string.todos_pedidos)
    } else {
        stringResource(R.string.meus_pedidos)
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (onNovoPedido != null) {
                ExtendedFloatingActionButton(
                    onClick = onNovoPedido,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.novo_pedido)) }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = { pedidosViewModel.ordenarDescendente.value = !ordenarDesc }) {
                    Icon(
                        if (ordenarDesc) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                        contentDescription = stringResource(R.string.ordenar_data)
                    )
                }
            }

            OutlinedTextField(
                value = pesquisa,
                onValueChange = { pedidosViewModel.pesquisa.value = it },
                placeholder = { Text(stringResource(R.string.pesquisar)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = filtroEstado == null,
                    onClick = { pedidosViewModel.filtroEstado.value = null },
                    label = { Text(stringResource(R.string.filtro_todos)) }
                )
                EstadoPedido.entries.forEach { estado ->
                    FilterChip(
                        selected = filtroEstado == estado,
                        onClick = {
                            pedidosViewModel.filtroEstado.value =
                                if (filtroEstado == estado) null else estado
                        },
                        label = { Text(stringResource(estado.label)) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                erro != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.erro_carregar_dados),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    val lista = pedidosViewModel.pedidosFiltrados()
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.sem_pedidos))
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(lista, key = { it.id ?: 0L }) { pedido ->
                                PedidoCard(
                                    pedido = pedido,
                                    mostrarAutor = perfil?.isAdmin == true,
                                    onClick = { pedido.id?.let(onAbrirPedido) }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido, mostrarAutor: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pedido.categorias?.nome ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(stringResource(pedido.estadoEnum.label)) }
                )
            }
            Text(pedido.localizacao, style = MaterialTheme.typography.bodyMedium)
            Text(
                pedido.descricao,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(pedido.dataFormatada, style = MaterialTheme.typography.labelSmall)
                if (mostrarAutor && pedido.profiles != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        pedido.profiles.nome,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
