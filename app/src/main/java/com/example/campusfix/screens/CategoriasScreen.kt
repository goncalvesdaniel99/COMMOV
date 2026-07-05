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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.data.Categoria
import com.example.campusfix.viewmodel.CategoriasViewModel

@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    modifier: Modifier = Modifier
) {
    val categorias by viewModel.categorias.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var novoNome by remember { mutableStateOf("") }
    var erroNovoNome by remember { mutableStateOf<Int?>(null) }
    var categoriaEmEdicao by remember { mutableStateOf<Categoria?>(null) }
    var categoriaAEliminar by remember { mutableStateOf<Categoria?>(null) }

    LaunchedEffect(Unit) { viewModel.carregar() }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.gerir_categorias), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it; erroNovoNome = null },
                label = { Text(stringResource(R.string.nome_categoria)) },
                isError = erroNovoNome != null,
                supportingText = { erroNovoNome?.let { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (novoNome.isBlank()) {
                    erroNovoNome = R.string.erro_campo_obrigatorio
                } else {
                    viewModel.criar(novoNome.trim())
                    novoNome = ""
                }
            }) {
                Text(stringResource(R.string.btn_adicionar))
            }
        }

        if (erro != null) {
            Text(
                stringResource(R.string.erro_guardar),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))

        if (categorias.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.sem_categorias))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categorias, key = { it.id ?: 0L }) { categoria ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                categoria.nome,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { categoriaEmEdicao = categoria }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.editar)
                                )
                            }
                            IconButton(onClick = { categoriaAEliminar = categoria }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.eliminar),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    categoriaEmEdicao?.let { categoria ->
        var nomeEditado by remember(categoria) { mutableStateOf(categoria.nome) }
        AlertDialog(
            onDismissRequest = { categoriaEmEdicao = null },
            title = { Text(stringResource(R.string.editar_categoria)) },
            text = {
                OutlinedTextField(
                    value = nomeEditado,
                    onValueChange = { nomeEditado = it },
                    label = { Text(stringResource(R.string.nome_categoria)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nomeEditado.isNotBlank()) {
                        viewModel.editar(categoria.id!!, nomeEditado.trim())
                        categoriaEmEdicao = null
                    }
                }) {
                    Text(stringResource(R.string.btn_guardar))
                }
            },
            dismissButton = {
                TextButton(onClick = { categoriaEmEdicao = null }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    categoriaAEliminar?.let { categoria ->
        AlertDialog(
            onDismissRequest = { categoriaAEliminar = null },
            title = { Text(stringResource(R.string.confirmar_eliminar_categoria)) },
            text = { Text(categoria.nome) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(categoria.id!!)
                    categoriaAEliminar = null
                }) {
                    Text(stringResource(R.string.confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { categoriaAEliminar = null }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}
