package com.example.campusfix.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.campusfix.R
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.CategoriasViewModel
import com.example.campusfix.viewmodel.PedidosViewModel

private data class TabItem(val labelRes: Int, val icon: ImageVector)

@Composable
fun MainTabsScreen(
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    categoriasViewModel: CategoriasViewModel,
    onNovoPedido: () -> Unit,
    onAbrirPedido: (Long) -> Unit
) {
    val perfil by authViewModel.perfil.collectAsState()

    // Sessão restaurada mas perfil ainda a carregar: evita mostrar as tabs erradas
    if (perfil == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val isAdmin = perfil?.isAdmin == true

    val tabs = if (isAdmin) {
        listOf(
            TabItem(R.string.tab_pedidos, Icons.AutoMirrored.Filled.ListAlt),
            TabItem(R.string.tab_categorias, Icons.Filled.Category),
            TabItem(R.string.tab_estatisticas, Icons.Filled.BarChart),
            TabItem(R.string.tab_perfil, Icons.Filled.Person)
        )
    } else {
        listOf(
            TabItem(R.string.tab_pedidos, Icons.AutoMirrored.Filled.ListAlt),
            TabItem(R.string.tab_historico, Icons.Filled.History),
            TabItem(R.string.tab_perfil, Icons.Filled.Person)
        )
    }

    var tabSelecionada by rememberSaveable { mutableIntStateOf(0) }
    if (tabSelecionada >= tabs.size) tabSelecionada = 0

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = tabSelecionada == index,
                        onClick = { tabSelecionada = index },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (isAdmin) {
            when (tabSelecionada) {
                0 -> PedidosListScreen(
                    authViewModel, pedidosViewModel,
                    onNovoPedido = null,
                    onAbrirPedido = onAbrirPedido,
                    modifier = modifier
                )
                1 -> CategoriasScreen(categoriasViewModel, modifier)
                2 -> EstatisticasScreen(pedidosViewModel, authViewModel, modifier)
                else -> PerfilScreen(authViewModel, modifier)
            }
        } else {
            when (tabSelecionada) {
                0 -> PedidosListScreen(
                    authViewModel, pedidosViewModel,
                    onNovoPedido = onNovoPedido,
                    onAbrirPedido = onAbrirPedido,
                    modifier = modifier
                )
                1 -> HistoricoScreen(authViewModel, pedidosViewModel, onAbrirPedido, modifier)
                else -> PerfilScreen(authViewModel, modifier)
            }
        }
    }
}
