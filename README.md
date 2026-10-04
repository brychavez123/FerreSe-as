# FerreSeñas

> ⚠️ **Proyecto académico.** Entrega de la asignatura Desarrollo de Aplicaciones Móviles (Duoc UC), no un producto listo para producción.

FerreSeñas es una aplicación móvil Android de accesibilidad, pensada para que
**personas con discapacidad sensorial auditiva** puedan comunicarse con
facilidad al comprar en una ferretería.

## ¿Qué problema resuelve?

Explicar verbalmente qué producto se necesita, en qué cantidad y de qué
medida no siempre es posible para una persona sorda o hipoacúsica, y no
todos los vendedores conocen lengua de señas. FerreSeñas resuelve esto
permitiendo armar un **mensaje visual claro** (por ejemplo: *"Hola,
necesito comprar 10 tornillos, de esta medida: 1/2 pulgada. ¿Me puede
ayudar, por favor?"*) que el cliente simplemente le muestra en pantalla,
o le lee en voz alta la propia aplicación, al vendedor.

## Funcionalidades

- **Inicio de sesión** y **registro de usuario** contra una base de datos
  SQLite local. Las contraseñas se guardan como **hash** (PBKDF2 con sal),
  nunca en texto plano.
- **Recordarme**: si se marca al iniciar sesión, la app entra directo al
  Inicio la próxima vez que se abra. Cerrar sesión lo borra.
- **Recuperar contraseña**: permite definir una contraseña nueva para un
  correo registrado.
- **Política de privacidad**, con una tabla de los datos que se recopilan
  y para qué se usan, accesible desde Registro.
- **Catálogo de productos** de ferretería, con filtro rápido por categoría
  (Fijación, Pintura, Medición, Herramientas, Eléctrico) y fotos reales de
  cada producto.
- **Constructor de mensajes visuales**: selecciona cantidad, medida y el
  tipo de mensaje (compra o consulta de disponibilidad), y genera una
  oración completa y natural pensada para mostrar al vendedor. Al
  generarlo, la app pasa a una **pantalla completa** con el texto
  autoajustable al espacio disponible.
- **Mensaje personalizado**: alternativa al mensaje generado por producto,
  para cuando el usuario necesita preguntar otra cosa que no está en el
  catálogo.
- **Lectura en voz alta (texto a voz)**: el mensaje se lee automáticamente
  si el usuario prefiere comunicarse hablando, y siempre está disponible
  con el botón "Escuchar mensaje".
- **Historial** de mensajes guardado en la base de datos: se mantiene al
  cerrar la app y cada usuario ve solo los suyos. Se puede **editar** el
  texto de un mensaje, **borrar** uno o **vaciar** el historial.
- **Perfil** de usuario: avatar personalizable (foto desde la galería),
  edición del nombre, preferencia de comunicación y notificaciones,
  cierre de sesión y **eliminación de la cuenta** (borra también sus
  mensajes).
- Todas las acciones que borran datos piden **confirmación** con un
  diálogo que respeta el alto contraste y el tamaño de letra.
- **Configuración de accesibilidad**: alto contraste, tamaño de letra
  ajustable para toda la aplicación y vibración de confirmación al
  generar o guardar un mensaje. Las preferencias se guardan en el
  dispositivo y se mantienen al cerrar la app.
- Navegación con menú inferior (Inicio / Historial / Perfil / Ajustes) y
  barra superior con el nombre y correo del usuario.

### Operaciones CRUD

| Operación | Dónde |
|---|---|
| Crear | Registrar usuario, guardar mensaje (Constructor y Mensaje personalizado) |
| Consultar | Historial, Perfil, inicio de sesión |
| Modificar | Nombre y preferencias en Perfil, texto de un mensaje en Historial, contraseña en Recuperar |
| Eliminar | Borrar un mensaje, vaciar historial, eliminar cuenta |

## Tecnologías

- **Kotlin** + **Jetpack Compose** (UI 100% declarativa)
- **Material Design 3**
- **Navigation Compose** para la navegación entre pantallas
- **SQLite** nativo de Android con `SQLiteOpenHelper` (sin Room), con
  consultas parametrizadas y claves foráneas con borrado en cascada
- **ViewModel** + **corrutinas** (`viewModelScope` y `Dispatchers.IO`) para
  consultar la base sin bloquear la pantalla
- **SharedPreferences** para las preferencias de accesibilidad y la
  sesión recordada
- **PBKDF2** (`javax.crypto`) para el hash de contraseñas
- **TextToSpeech** de Android para la lectura en voz alta
- **JUnit 4**, **AndroidX Test** y **Compose UI Test** para las pruebas
- **Android Gradle Plugin 9.3.0** / **Gradle 9.7.1**

### Estructura de datos

- `data/FerreSenasDbHelper.kt`: base `ferresenas.db` con las tablas
  `usuarios` (correo como clave primaria) y `mensajes` (id
  autoincremental, correo del usuario como clave foránea). Se precargan
  5 usuarios de prueba.
- `data/Usuario.kt` y `data/Producto.kt`: modelos y repositorios
  (`RepositorioUsuarios`, `RepositorioMensajes`) que usan el helper.
- `ui/viewmodel/`: `SesionViewModel` (cuenta y sesión) y
  `MensajesViewModel` (historial).
- El catálogo de productos sigue definido en código (`CatalogoProductos`).

### Funciones de Kotlin integradas

- **Funciones de orden superior e inline**: `ejecutarSi()` y
  `primerError()` (`Utilidades.kt`), usadas para validar los formularios de
  Registro y Recuperar contraseña sin repetir un bloque `when`.
- **Funciones lambda y operaciones con colecciones**: `filter`, `find`,
  `forEach`, `map`, `distinct` y `sortedBy` en el catálogo, los
  repositorios y las utilidades de la app.
- **Funciones y propiedades de extensión**: sobre `String`, `Int`,
  `Context`, `Usuario`, `List<Producto>` y `List<MensajeHistorial>`
  (`Utilidades.kt`, `Preferencias.kt`, `Accesibilidad.kt`, `Usuario.kt`).
- **Manejo de errores y excepciones**: `try/catch/finally` en
  `textoACantidadSegura()` para validar la cantidad ingresada sin que la
  app se detenga.

## Usuarios de prueba

Todos con contraseña `1234`:

| Nombre | Correo |
|---|---|
| Valentina Muñoz | valentina@ferresenas.cl |
| Roberto Fernández | roberto@ferresenas.cl |
| Camila Reyes | camila@ferresenas.cl |
| Diego Castro | diego@ferresenas.cl |
| Javiera Morales | javiera@ferresenas.cl |

## Cómo ejecutar el proyecto

1. Clonar este repositorio.
2. Abrir la carpeta del proyecto en **Android Studio** (versión Quail o
   superior, compatible con AGP 9.x).
3. Dejar que Gradle sincronice (puede descargar el SDK Platform 37 y
   Build-Tools 36.0.0 si no los tienes instalados).
4. Ejecutar en un emulador o dispositivo Android 7.0 (API 24) o superior.

No hay claves de API ni backend externo que configurar: la base de datos
se crea sola en el dispositivo la primera vez que se abre la app.

## Cómo correr las pruebas

Desde la carpeta del proyecto (en Windows usa `gradlew.bat`, en macOS o
Linux `./gradlew`):

```bash
# pruebas unitarias (JUnit, no necesitan emulador)
gradlew.bat testDebugUnitTest

# pruebas instrumentadas (CRUD de SQLite y UI del Login),
# necesitan un emulador encendido o un celular conectado
gradlew.bat connectedDebugAndroidTest
```

Los reportes quedan en `app/build/reports/tests/testDebugUnitTest/index.html`
y `app/build/reports/androidTests/connected/debug/index.html`.

| Archivo | Qué prueba |
|---|---|
| `app/src/test/.../UtilidadesTest.kt` | `textoACantidadSegura`, `primerError`, `categoriasDisponibles` |
| `app/src/test/.../ConstruirMensajeTest.kt` | `construirMensaje` |
| `app/src/test/.../SeguridadTest.kt` | hash y verificación de contraseñas |
| `app/src/androidTest/.../FerreSenasDbHelperTest.kt` | CRUD completo de la base, con una base en memoria |
| `app/src/androidTest/.../LoginScreenTest.kt` | pantalla de Login con Compose UI Test |

## Generar el APK firmado

La firma de release lee sus datos desde `keystore.properties` (en la raíz
del proyecto). Ese archivo y el keystore están en `.gitignore` y **no se
suben al repositorio**. Hay una plantilla en `keystore.properties.example`.

1. Crear el keystore (una sola vez; `keytool` viene con el JDK):

   ```bash
   keytool -genkeypair -v -keystore ferresenas-release.jks -storetype PKCS12 -alias ferresenas -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Copiar `keystore.properties.example` como `keystore.properties` y
   completar la contraseña que elegiste.
3. Generar el APK:

   ```bash
   gradlew.bat assembleRelease
   ```

   Queda en `app/build/outputs/apk/release/app-release.apk`. Sin
   `keystore.properties`, se genera `app-release-unsigned.apk`.

4. Verificar la firma:

   ```bash
   apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk
   ```

## Limitaciones conocidas

- Los datos viven solo en el dispositivo (SQLite local): no hay
  sincronización con un servidor.
- La recuperación de contraseña no verifica la identidad por correo; en
  una app real habría que enviar un código al correo antes de permitir el
  cambio.
- La foto de perfil se mantiene solo mientras la app está abierta.
- No incluye todavía voz a texto (dictado); solo lectura en voz alta.
