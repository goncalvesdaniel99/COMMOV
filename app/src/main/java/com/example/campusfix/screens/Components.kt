package com.example.campusfix.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.campusfix.R
import com.example.campusfix.data.EstadoPedido
import com.example.campusfix.data.Pedido

/** Header de página estilo iOS: botão circular de voltar + título grande, sem TopAppBar. */
@Composable
fun HeaderVoltar(titulo: String, onVoltar: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onVoltar)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.voltar),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
    }
}

/** Pill de estado com cor própria por estado (funciona em modo claro e escuro). */
@Composable
fun EstadoPill(estado: EstadoPedido) {
    val escuro = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val cor = when (estado) {
        EstadoPedido.SUBMETIDO -> if (escuro) Color(0xFFFFC46B) else Color(0xFFB45309)
        EstadoPedido.EM_ANALISE -> if (escuro) Color(0xFF8FB7FF) else Color(0xFF2563EB)
        EstadoPedido.CONCLUIDO -> if (escuro) Color(0xFF7ED9A0) else Color(0xFF15803D)
        EstadoPedido.REJEITADO -> if (escuro) Color(0xFFFF9E9E) else Color(0xFFDC2626)
    }
    Surface(shape = RoundedCornerShape(50), color = cor.copy(alpha = 0.14f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Surface(shape = CircleShape, color = cor, modifier = Modifier.size(7.dp)) {}
            Spacer(Modifier.width(6.dp))
            Text(
                stringResource(estado.label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = cor
            )
        }
    }
}

/** Ícone associado ao nome da categoria. */
fun iconeCategoria(nome: String?): ImageVector {
    val n = (nome ?: "").lowercase()
    return when {
        "limp" in n -> Icons.Filled.CleaningServices
        "manut" in n -> Icons.Filled.Build
        "inform" in n || "comput" in n -> Icons.Filled.Computer
        "segur" in n -> Icons.Filled.Security
        "jardi" in n -> Icons.Filled.Yard
        else -> Icons.Filled.Handyman
    }
}

/** Cartão de pedido: ícone da categoria, estado colorido, descrição e rodapé. */
@Composable
fun PedidoCard(pedido: Pedido, mostrarAutor: Boolean, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            iconeCategoria(pedido.categorias?.nome),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        pedido.categorias?.nome ?: "-",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        pedido.localizacao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                EstadoPill(pedido.estadoEnum)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                pedido.descricao,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row {
                Text(
                    pedido.dataFormatada,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (mostrarAutor && pedido.profiles != null) {
                    Spacer(Modifier.width(10.dp))
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
