package cl.duoc.ferresenas.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.ui.screens.ConstructorMensajeScreen
import cl.duoc.ferresenas.ui.screens.HomeScreen
import cl.duoc.ferresenas.ui.screens.LoginScreen
import cl.duoc.ferresenas.ui.screens.MensajePersonalizadoScreen
import cl.duoc.ferresenas.ui.screens.PoliticaPrivacidadScreen
import cl.duoc.ferresenas.ui.screens.RecuperarContrasenaScreen
import cl.duoc.ferresenas.ui.screens.RegistroScreen
import cl.duoc.ferresenas.ui.viewmodel.SesionViewModel

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar"
    const val HOME = "home"
    const val POLITICA_PRIVACIDAD = "politica_privacidad"
    const val MENSAJE_PERSONALIZADO = "mensaje_personalizado"
    const val CONSTRUCTOR = "constructor/{productoId}"

    fun constructorConId(productoId: Int) = "constructor/$productoId"
}

@Composable
fun FerreSenasNavGraph(
    navController: NavHostController = rememberNavController(),
    sesionViewModel: SesionViewModel = viewModel()
) {
    // primero reviso si habia una sesion guardada con "Recordarme". mientras
    // se consulta la base muestro un circulo de carga en vez del Login
    var revisandoSesion by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        sesionViewModel.restaurarSesion { revisandoSesion = false }
    }
    if (revisandoSesion) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // con remember se calcula una sola vez. si cambiara despues, el NavHost
    // armaria el grafo de nuevo y se perderia la navegacion
    val inicio = remember { if (SesionActual.usuarioActual != null) Rutas.HOME else Rutas.LOGIN }

    NavHost(navController = navController, startDestination = inicio) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                onIrARecuperarContrasena = { navController.navigate(Rutas.RECUPERAR) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVolverLogin = { navController.popBackStack() },
                onVerPoliticaPrivacidad = { navController.navigate(Rutas.POLITICA_PRIVACIDAD) }
            )
        }

        composable(Rutas.POLITICA_PRIVACIDAD) {
            PoliticaPrivacidadScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.RECUPERAR) {
            RecuperarContrasenaScreen(
                onVolverLogin = { navController.popBackStack() }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                onProductoSeleccionado = { producto ->
                    navController.navigate(Rutas.constructorConId(producto.id))
                },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },
                onCrearMensajePersonalizado = { navController.navigate(Rutas.MENSAJE_PERSONALIZADO) }
            )
        }

        composable(Rutas.MENSAJE_PERSONALIZADO) {
            MensajePersonalizadoScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.CONSTRUCTOR,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: -1
            val producto = CatalogoProductos.buscarPorId(productoId)
            if (producto != null) {
                ConstructorMensajeScreen(
                    producto = producto,
                    onVolverCatalogo = { navController.popBackStack() }
                )
            }
        }
    }
}
