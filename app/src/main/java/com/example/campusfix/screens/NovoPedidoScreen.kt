package com.example.campusfix.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campusfix.R
import com.example.campusfix.data.Categoria
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.CategoriasViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoPedidoScreen(
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    categoriasViewModel: CategoriasViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val perfil by authViewModel.perfil.collectAsState()
    val categorias by categoriasViewModel.categorias.collectAsState()
    val loading by pedidosViewModel.loading.collectAsState()
    val erro by pedidosViewModel.erro.collectAsState()

    var categoriaSelecionada by remember { mutableStateOf<Categoria?>(null) }
    var dropdownAberto by remember { mutableStateOf(false) }
    var localizacao by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    var erroCategoria by remember { mutableStateOf<Int?>(null) }
    var erroLocalizacao by remember { mutableStateOf<Int?>(null) }
    var erroDescricao by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        categoriasViewModel.carregar()
        pedidosViewModel.limparErro()
    }

    val fotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> fotoUri = uri }

    fun validar(): Boolean {
        erroCategoria = if (categoriaSelecionada == null) R.string.erro_campo_obrigatorio else null
        erroLocalizacao = if (localizacao.isBlank()) R.string.erro_campo_obrigatorio else null
        erroDescricao = if (descricao.isBlank()) R.string.erro_campo_obrigatorio else null
        return listOf(erroCategoria, erroLocalizacao, erroDescricao).all { it == null }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        HeaderVoltar(stringResource(R.string.novo_pedido), onVoltar)
        Column {
            ExposedDropdownMenuBox(
                expanded = dropdownAberto,
                onExpandedChange = { dropdownAberto = it }
            ) {
                OutlinedTextField(
                    value = categoriaSelecionada?.nome ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.categoria)) },
                    isError = erroCategoria != null,
                    supportingText = { erroCategoria?.let { Text(stringResource(it)) } },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAberto)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = dropdownAberto,
                    onDismissRequest = { dropdownAberto = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nome) },
                            onClick = {
                                categoriaSelecionada = categoria
                                erroCategoria = null
                                dropdownAberto = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = localizacao,
                onValueChange = { localizacao = it; erroLocalizacao = null },
                label = { Text(stringResource(R.string.localizacao)) },
                isError = erroLocalizacao != null,
                supportingText = { erroLocalizacao?.let { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it; erroDescricao = null },
                label = { Text(stringResource(R.string.descricao)) },
                isError = erroDescricao != null,
                supportingText = { erroDescricao?.let { Text(stringResource(it)) } },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row {
                OutlinedButton(onClick = {
                    fotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.escolher_foto))
                }
                if (fotoUri != null) {
                    IconButton(onClick = { fotoUri = null }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.remover_foto)
                        )
                    }
                }
            }
            fotoUri?.let { uri ->
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = stringResource(R.string.fotografia),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            if (erro != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.erro_guardar),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val userId = perfil?.id ?: return@Button
                    if (validar()) {
                        val bytes = fotoUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                        pedidosViewModel.criarPedido(
                            userId = userId,
                            categoriaId = categoriaSelecionada!!.id!!,
                            localizacao = localizacao.trim(),
                            descricao = descricao.trim(),
                            fotoBytes = bytes,
                            onSucesso = onVoltar
                        )
                    }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.btn_submeter))
                }
            }
        }
    }
}
