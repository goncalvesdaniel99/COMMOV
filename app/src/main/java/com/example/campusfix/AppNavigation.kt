package com.example.campusfix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campusfix.screens.DetalhePedidoScreen
import com.example.campusfix.screens.LoginScreen
import com.example.campusfix.screens.MainTabsScreen
import com.example.campusfix.screens.NovoPedidoScreen
import com.example.campusfix.screens.RegisterScreen
import com.example.campusfix.viewmodel.AuthViewModel
import com.example.campusfix.viewmodel.CategoriasViewModel
import com.example.campusfix.viewmodel.PedidosViewModel
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val pedidosViewModel: PedidosViewModel = viewModel()
    val categoriasViewModel: CategoriasViewModel = viewModel()

    val sessionStatus by authViewModel.sessionStatus.collectAsState()

    when (sessionStatus) {
        is SessionStatus.Initializing -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is SessionStatus.Authenticated -> {
            AppAutenticada(authViewModel, pedidosViewModel, categoriasViewModel)
        }

        else -> {
            AuthNavHost(authViewModel)
        }
    }
}

@Composable
private fun AuthNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onIrParaRegisto = { navController.navigate(Routes.REGISTO) }
            )
        }
        composable(Routes.REGISTO) {
            RegisterScreen(
                viewModel = authViewModel,
                onIrParaLogin = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun AppAutenticada(
    authViewModel: AuthViewModel,
    pedidosViewModel: PedidosViewModel,
    categoriasViewModel: CategoriasViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) {
            MainTabsScreen(
                authViewModel = authViewModel,
                pedidosViewModel = pedidosViewModel,
                categoriasViewModel = categoriasViewModel,
                onNovoPedido = { navController.navigate(Routes.NOVO_PEDIDO) },
                onAbrirPedido = { id -> navController.navigate(Routes.detalhePedido(id)) }
            )
        }
        composable(Routes.NOVO_PEDIDO) {
            NovoPedidoScreen(
                authViewModel = authViewModel,
                pedidosViewModel = pedidosViewModel,
                categoriasViewModel = categoriasViewModel,
                onVoltar = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.DETALHE_PEDIDO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getLong("pedidoId") ?: 0L
            DetalhePedidoScreen(
                pedidoId = pedidoId,
                authViewModel = authViewModel,
                pedidosViewModel = pedidosViewModel,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
