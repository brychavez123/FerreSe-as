package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.categoriasDisponibles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onProductoSeleccionado: (Producto) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(CatalogoProductos.CATEGORIA_TODOS) }

    // categoriasDisponibles esta en Utilidades.kt, saca las categorias que
    // hay en el catalogo sin tener que escribirlas todas a mano de nuevo
    val categorias = listOf(CatalogoProductos.CATEGORIA_TODOS) + CatalogoProductos.productos.categoriasDisponibles

    val productosFiltrados = if (categoriaSeleccionada == CatalogoProductos.CATEGORIA_TODOS) {
        CatalogoProductos.productos
    } else {
        CatalogoProductos.productos.filter { it.categoria == categoriaSeleccionada }
    }.sortedBy { it.nombre }

    // Grilla de productos. Adaptive en vez de Fixed(2): pone tantas columnas
    // como quepan (2 en un celular vertical, mas en horizontal o tablet).
    // el titulo y los filtros van como primer item de la grilla para que
    // se desplacen junto con los productos, si no en horizontal casi no
    // quedaba espacio para ver la grilla
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categorias) { categoria ->
                        FilterChip(
                            selected = categoria == categoriaSeleccionada,
                            onClick = { categoriaSeleccionada = categoria },
                            label = { Text(categoria) }
                        )
                    }
                }
            }
        }

        items(productosFiltrados) { producto ->
            TarjetaProducto(producto = producto, onClick = { onProductoSeleccionado(producto) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaProducto(producto: Producto, onClick: () -> Unit) {
    val contexto = LocalContext.current
    val idImagen = remember(producto.imagenNombre) {
        contexto.resources.getIdentifier(producto.imagenNombre, "drawable", contexto.packageName)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (idImagen != 0) {
                    // Imagen real del producto, tomada de app/src/main/res/drawable/.
                    // Se agranda un poco (scale) para recortar el margen blanco que
                    // traen las fotos originales y que el producto se vea más grande.
                    Image(
                        painter = painterResource(id = idImagen),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.35f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Respaldo mientras no exista la imagen en res/drawable/
                    Text(text = producto.emoji, style = MaterialTheme.typography.headlineLarge)
                }
            }
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
