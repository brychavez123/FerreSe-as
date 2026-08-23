package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.data.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onProductoSeleccionado: (Producto) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(CatalogoProductos.CATEGORIA_TODOS) }
    val productosFiltrados = if (categoriaSeleccionada == CatalogoProductos.CATEGORIA_TODOS) {
        CatalogoProductos.productos
    } else {
        CatalogoProductos.productos.filter { it.categoria == categoriaSeleccionada }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "¿Qué necesitas hoy?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Selecciona un producto para armar tu mensaje",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Acciones rápidas: filtro por categoría
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            items(CatalogoProductos.categorias) { categoria ->
                FilterChip(
                    selected = categoria == categoriaSeleccionada,
                    onClick = { categoriaSeleccionada = categoria },
                    label = { Text(categoria) }
                )
            }
        }

        // Grilla de productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosFiltrados) { producto ->
                TarjetaProducto(producto = producto, onClick = { onProductoSeleccionado(producto) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaProducto(producto: Producto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = producto.emoji, style = MaterialTheme.typography.headlineLarge)
            Text(text = producto.nombre, style = MaterialTheme.typography.titleLarge)
        }
    }
}
