package com.example.campusfix

object Routes {
    const val LOGIN = "login"
    const val REGISTO = "registo"
    const val MAIN = "main"
    const val NOVO_PEDIDO = "novoPedido"
    const val DETALHE_PEDIDO = "detalhePedido/{pedidoId}"

    fun detalhePedido(id: Long) = "detalhePedido/$id"
}
