package cl.duoc.ferresenas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.ui.screens.ConstructorMensajeScreen
import cl.duoc.ferresenas.ui.screens.HomeScreen
import cl.duoc.ferresenas.ui.screens.LoginScreen
import cl.duoc.ferresenas.ui.screens.PoliticaPrivacidadScreen
import cl.duoc.ferresenas.ui.screens.RecuperarContrasenaScreen
import cl.duoc.ferresenas.ui.screens.RegistroScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar"
    const val HOME = "home"
    const val POLITICA_PRIVACIDAD = "politica_privacidad"
    const val CONSTRUCTOR = "constructor/{productoId}"

    fun constructorConId(productoId: Int) = "constructor/$productoId"
}

@Composable
fun FerreSenasNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

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
                }
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
