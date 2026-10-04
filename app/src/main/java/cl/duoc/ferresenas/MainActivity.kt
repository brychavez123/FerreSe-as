package cl.duoc.ferresenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.duoc.ferresenas.data.LectorDeVoz
import cl.duoc.ferresenas.data.PreferenciasApp
import cl.duoc.ferresenas.data.SesionGuardada
import cl.duoc.ferresenas.navigation.FerreSenasNavGraph
import cl.duoc.ferresenas.ui.theme.FerreSenasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenciasApp.inicializar(this)
        SesionGuardada.inicializar(this)
        LectorDeVoz.inicializar(this)
        setContent {
            FerreSenasApp()
        }
    }

    override fun onDestroy() {
        LectorDeVoz.liberar()
        super.onDestroy()
    }
}

@Composable
fun FerreSenasApp() {
    FerreSenasTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FerreSenasNavGraph()
        }
    }
}
