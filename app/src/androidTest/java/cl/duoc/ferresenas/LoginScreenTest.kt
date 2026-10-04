package cl.duoc.ferresenas

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.ferresenas.data.FerreSenasDbHelper
import cl.duoc.ferresenas.data.PreferenciaComunicacion
import cl.duoc.ferresenas.data.Seguridad
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.Usuario
import cl.duoc.ferresenas.ui.screens.LoginScreen
import cl.duoc.ferresenas.ui.theme.FerreSenasTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// prueba de UI del Login. el LoginScreen usa la base real de la app, asi
// que antes de cada prueba creo un usuario propio y despues lo borro
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val correoPrueba = "login.ui@test.cl"
    private val db by lazy {
        FerreSenasDbHelper.obtener(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun crearUsuario() {
        db.eliminarUsuario(correoPrueba)
        db.insertarUsuario(
            Usuario("Login UI", correoPrueba, Seguridad.hashContrasena("clave123"), PreferenciaComunicacion.ESCRIBIR, false)
        )
        SesionActual.usuarioActual = null
    }

    @After
    fun borrarUsuario() {
        db.eliminarUsuario(correoPrueba)
        SesionActual.usuarioActual = null
    }

    @Test
    fun credencialesCorrectas_entraAlHome() {
        var loginExitoso = false
        composeRule.setContent {
            FerreSenasTheme {
                LoginScreen(
                    onLoginExitoso = { loginExitoso = true },
                    onIrARegistro = {},
                    onIrARecuperarContrasena = {}
                )
            }
        }

        composeRule.onNodeWithText("Correo electrónico").performTextInput(correoPrueba)
        composeRule.onNodeWithText("Contraseña").performTextInput("clave123")
        composeRule.onNodeWithText("Ingresar").performClick()

        // la consulta corre en segundo plano, hay que esperar
        composeRule.waitUntil(timeoutMillis = 5_000) { loginExitoso }
        assertEquals(correoPrueba, SesionActual.usuarioActual?.correo)
    }

    @Test
    fun contrasenaIncorrecta_muestraError() {
        var loginExitoso = false
        composeRule.setContent {
            FerreSenasTheme {
                LoginScreen(
                    onLoginExitoso = { loginExitoso = true },
                    onIrARegistro = {},
                    onIrARecuperarContrasena = {}
                )
            }
        }

        composeRule.onNodeWithText("Correo electrónico").performTextInput(correoPrueba)
        composeRule.onNodeWithText("Contraseña").performTextInput("otra")
        composeRule.onNodeWithText("Ingresar").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasText("Correo o contraseña incorrectos", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertFalse(loginExitoso)
    }
}
