package cl.duoc.ferresenas.data

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// para no guardar la contraseña tal cual en la base de datos. uso PBKDF2
// que ya viene en Android (y en Java normal, asi se puede probar con JUnit)
// sin agregar ninguna libreria. se guarda como "sal:hash" en hexadecimal
object Seguridad {
    // con SHA1 porque la version con SHA256 recien existe desde Android 8
    // y la app parte en Android 7 (minSdk 24)
    private const val ALGORITMO = "PBKDF2WithHmacSHA1"
    private const val ITERACIONES = 10_000
    private const val LARGO_BITS = 256
    private const val LARGO_SAL = 16

    fun hashContrasena(contrasena: String): String {
        // la sal es aleatoria, asi dos usuarios con la misma clave no
        // quedan con el mismo hash
        val sal = ByteArray(LARGO_SAL).also { SecureRandom().nextBytes(it) }
        return "${sal.aHex()}:${calcular(contrasena, sal).aHex()}"
    }

    fun verificarContrasena(contrasena: String, guardado: String): Boolean {
        val partes = guardado.split(":")
        if (partes.size != 2) return false
        return try {
            val sal = partes[0].deHex()
            val esperado = partes[1].deHex()
            // MessageDigest.isEqual compara sin cortar antes, mas seguro que ==
            MessageDigest.isEqual(calcular(contrasena, sal), esperado)
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun calcular(contrasena: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun ByteArray.aHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.deHex(): ByteArray {
        require(length % 2 == 0) { "Hex inválido" }
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
