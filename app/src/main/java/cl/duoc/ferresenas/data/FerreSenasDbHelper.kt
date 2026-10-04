package cl.duoc.ferresenas.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// base de datos SQLite de la app, con SQLiteOpenHelper nativo de Android.
// guarda los usuarios y sus mensajes para que no se pierdan al cerrar la app.
// el catalogo de productos sigue fijo en Producto.kt, aca solo se guarda el id
//
// ojo: estos metodos son bloqueantes, por eso los repositorios los llaman
// dentro de withContext(Dispatchers.IO) y nunca desde el hilo principal
//
// nombreBd = null crea la base en memoria, se usa en las pruebas
class FerreSenasDbHelper(
    contexto: Context,
    nombreBd: String? = NOMBRE_BD
) : SQLiteOpenHelper(contexto, nombreBd, null, VERSION) {

    companion object {
        const val NOMBRE_BD = "ferresenas.db"
        // si cambio las tablas hay que subir esto para que corra onUpgrade
        const val VERSION = 1

        const val TABLA_USUARIOS = "usuarios"
        const val COL_CORREO = "correo"
        const val COL_NOMBRE = "nombre"
        const val COL_HASH = "hash_contrasena"
        const val COL_PREFERENCIA = "preferencia_comunicacion"
        const val COL_NOTIFICACIONES = "recibir_notificaciones"

        const val TABLA_MENSAJES = "mensajes"
        const val COL_ID = "id"
        const val COL_CORREO_USUARIO = "correo_usuario"
        const val COL_PRODUCTO_ID = "producto_id"
        const val COL_TEXTO = "texto"
        const val COL_FECHA = "fecha"

        // una sola instancia para toda la app, asi no se abren varias
        // conexiones a la misma base
        @Volatile
        private var instancia: FerreSenasDbHelper? = null

        fun obtener(contexto: Context): FerreSenasDbHelper =
            instancia ?: synchronized(this) {
                instancia ?: FerreSenasDbHelper(contexto.applicationContext).also { instancia = it }
            }
    }

    // sin esto SQLite ignora las FOREIGN KEY y no funciona el ON DELETE CASCADE
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA_USUARIOS (
                $COL_CORREO TEXT PRIMARY KEY NOT NULL,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_HASH TEXT NOT NULL,
                $COL_PREFERENCIA TEXT NOT NULL,
                $COL_NOTIFICACIONES INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
        // ON DELETE CASCADE: si se borra la cuenta se borran sus mensajes solos
        db.execSQL(
            """
            CREATE TABLE $TABLA_MENSAJES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CORREO_USUARIO TEXT NOT NULL,
                $COL_PRODUCTO_ID INTEGER,
                $COL_TEXTO TEXT NOT NULL,
                $COL_FECHA INTEGER NOT NULL,
                FOREIGN KEY ($COL_CORREO_USUARIO) REFERENCES $TABLA_USUARIOS($COL_CORREO)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
        precargarUsuarios(db)
    }

    // version simple: borra todo y lo vuelve a crear. para una app de curso
    // basta, en una app real habria que migrar los datos con ALTER TABLE
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_MENSAJES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        onCreate(db)
    }

    // los mismos 5 usuarios de prueba que estaban en memoria, todos con
    // clave "1234" pero guardada como hash
    private fun precargarUsuarios(db: SQLiteDatabase) {
        listOf(
            Triple("Valentina Muñoz", "valentina@ferresenas.cl", PreferenciaComunicacion.ESCRIBIR to true),
            Triple("Roberto Fernández", "roberto@ferresenas.cl", PreferenciaComunicacion.HABLAR to false),
            Triple("Camila Reyes", "camila@ferresenas.cl", PreferenciaComunicacion.AMBAS to true),
            Triple("Diego Castro", "diego@ferresenas.cl", PreferenciaComunicacion.ESCRIBIR to false),
            Triple("Javiera Morales", "javiera@ferresenas.cl", PreferenciaComunicacion.HABLAR to true)
        ).forEach { (nombre, correo, prefs) ->
            val usuario = Usuario(nombre, correo, Seguridad.hashContrasena("1234"), prefs.first, prefs.second)
            db.insert(TABLA_USUARIOS, null, usuario.aContentValues())
        }
    }

    // ---------- usuarios ----------

    // devuelve false si el correo ya existia (insert da -1 por la clave primaria)
    fun insertarUsuario(usuario: Usuario): Boolean =
        writableDatabase.insert(TABLA_USUARIOS, null, usuario.aContentValues()) != -1L

    fun buscarUsuario(correo: String): Usuario? =
        readableDatabase.query(
            TABLA_USUARIOS, null, "$COL_CORREO = ?", arrayOf(correo.normalizado()),
            null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.aUsuario() else null
        }

    // actualiza nombre y preferencias (el correo no se cambia porque es la clave)
    fun actualizarUsuario(usuario: Usuario): Int {
        val valores = ContentValues().apply {
            put(COL_NOMBRE, usuario.nombre)
            put(COL_PREFERENCIA, usuario.preferenciaComunicacion.name)
            put(COL_NOTIFICACIONES, if (usuario.recibirNotificaciones) 1 else 0)
        }
        return writableDatabase.update(TABLA_USUARIOS, valores, "$COL_CORREO = ?", arrayOf(usuario.correo.normalizado()))
    }

    fun actualizarHashContrasena(correo: String, nuevoHash: String): Int {
        val valores = ContentValues().apply { put(COL_HASH, nuevoHash) }
        return writableDatabase.update(TABLA_USUARIOS, valores, "$COL_CORREO = ?", arrayOf(correo.normalizado()))
    }

    fun eliminarUsuario(correo: String): Int =
        writableDatabase.delete(TABLA_USUARIOS, "$COL_CORREO = ?", arrayOf(correo.normalizado()))

    // ---------- mensajes ----------

    // devuelve el id nuevo, o -1 si fallo (por ejemplo si el usuario no existe)
    fun insertarMensaje(correo: String, productoId: Int?, texto: String, fecha: Long = System.currentTimeMillis()): Long {
        val valores = ContentValues().apply {
            put(COL_CORREO_USUARIO, correo.normalizado())
            if (productoId != null) put(COL_PRODUCTO_ID, productoId) else putNull(COL_PRODUCTO_ID)
            put(COL_TEXTO, texto)
            put(COL_FECHA, fecha)
        }
        return writableDatabase.insert(TABLA_MENSAJES, null, valores)
    }

    // solo los mensajes de ese correo, el mas nuevo primero
    fun mensajesDeUsuario(correo: String): List<MensajeGuardado> =
        readableDatabase.query(
            TABLA_MENSAJES, null, "$COL_CORREO_USUARIO = ?", arrayOf(correo.normalizado()),
            null, null, "$COL_FECHA DESC, $COL_ID DESC"
        ).use { cursor ->
            val lista = mutableListOf<MensajeGuardado>()
            while (cursor.moveToNext()) {
                val columnaProducto = cursor.getColumnIndexOrThrow(COL_PRODUCTO_ID)
                lista.add(
                    MensajeGuardado(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        productoId = if (cursor.isNull(columnaProducto)) null else cursor.getInt(columnaProducto),
                        texto = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXTO)),
                        fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COL_FECHA))
                    )
                )
            }
            lista
        }

    // pido tambien el correo en el WHERE para que nadie edite mensajes de otro
    fun actualizarTextoMensaje(id: Long, correo: String, nuevoTexto: String): Int {
        val valores = ContentValues().apply { put(COL_TEXTO, nuevoTexto) }
        return writableDatabase.update(
            TABLA_MENSAJES, valores, "$COL_ID = ? AND $COL_CORREO_USUARIO = ?",
            arrayOf(id.toString(), correo.normalizado())
        )
    }

    fun eliminarMensaje(id: Long, correo: String): Int =
        writableDatabase.delete(
            TABLA_MENSAJES, "$COL_ID = ? AND $COL_CORREO_USUARIO = ?",
            arrayOf(id.toString(), correo.normalizado())
        )

    fun eliminarMensajesDeUsuario(correo: String): Int =
        writableDatabase.delete(TABLA_MENSAJES, "$COL_CORREO_USUARIO = ?", arrayOf(correo.normalizado()))

    // ---------- conversiones ----------

    private fun Usuario.aContentValues() = ContentValues().apply {
        put(COL_CORREO, correo.normalizado())
        put(COL_NOMBRE, nombre)
        put(COL_HASH, hashContrasena)
        put(COL_PREFERENCIA, preferenciaComunicacion.name)
        put(COL_NOTIFICACIONES, if (recibirNotificaciones) 1 else 0)
    }

    private fun Cursor.aUsuario() = Usuario(
        nombre = getString(getColumnIndexOrThrow(COL_NOMBRE)),
        correo = getString(getColumnIndexOrThrow(COL_CORREO)),
        hashContrasena = getString(getColumnIndexOrThrow(COL_HASH)),
        // si por algun motivo viene un valor raro, queda en ESCRIBIR
        preferenciaComunicacion = PreferenciaComunicacion.entries
            .find { it.name == getString(getColumnIndexOrThrow(COL_PREFERENCIA)) }
            ?: PreferenciaComunicacion.ESCRIBIR,
        recibirNotificaciones = getInt(getColumnIndexOrThrow(COL_NOTIFICACIONES)) == 1
    )

    // antes se comparaban los correos con ignoreCase, ahora los guardo
    // siempre en minuscula y sin espacios para que sea lo mismo
    private fun String.normalizado() = trim().lowercase()
}

// fila tal cual sale de la tabla mensajes, el repositorio la pasa a
// MensajeHistorial (con el Producto y la fecha ya formateada)
data class MensajeGuardado(
    val id: Long,
    val productoId: Int?,
    val texto: String,
    val fecha: Long
)
