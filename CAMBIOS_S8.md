# Cambios de la Semana 8: FerreSeñas 2.0

Este documento explica en lenguaje simple qué se cambió en la app, por qué
y cómo funciona cada parte. Antes, los usuarios y los mensajes vivían en
listas en memoria y se perdían al cerrar la app. Ahora se guardan en una
base de datos SQLite dentro del celular.

Cada etapa quedó en su propio commit de Git:

| Commit | Etapa |
|---|---|
| Etapa 1 | Base de datos SQLite y hash de contraseñas |
| Etapa 2 | Conectar pantallas a SQLite con ViewModel y sesión recordada |
| Etapa 3 | CRUD completo con diálogos de confirmación |
| Etapa 4 | Pruebas unitarias e instrumentadas |
| Ajustes | Arreglos encontrados al probar en el emulador |
| Etapa 5 | Versión 2.0 y firma de release |
| Etapa 6 | README y este documento |
| Manejo de errores | try/catch solo donde puede fallar (retroalimentación S5) |
| Voz a texto | El vendedor puede responder hablando (retroalimentación S5) |

---

## 1. Persistencia con SQLite

### Archivo nuevo: `data/FerreSenasDbHelper.kt`

**Qué es:** una clase que extiende `SQLiteOpenHelper`, la herramienta que
trae Android para crear y abrir una base de datos SQLite. No se agregó
ninguna librería (ni Room ni otra).

**Tablas:**

| Tabla `usuarios` | Tipo | Nota |
|---|---|---|
| `correo` | TEXT | **Clave primaria** (no puede repetirse) |
| `nombre` | TEXT | |
| `hash_contrasena` | TEXT | La contraseña "cifrada", nunca la real |
| `preferencia_comunicacion` | TEXT | ESCRIBIR, HABLAR o AMBAS |
| `recibir_notificaciones` | INTEGER | 1 = sí, 0 = no |

| Tabla `mensajes` | Tipo | Nota |
|---|---|---|
| `id` | INTEGER | Clave primaria **autoincremental** |
| `correo_usuario` | TEXT | **Clave foránea** hacia `usuarios.correo` |
| `producto_id` | INTEGER | Puede ser nulo (mensajes personalizados) |
| `texto` | TEXT | El mensaje |
| `fecha` | INTEGER | Fecha en milisegundos |

**Cómo funciona:**

- `onCreate` se ejecuta una sola vez, la primera vez que se abre la app.
  Crea las dos tablas y **precarga los 5 usuarios de prueba** (con
  contraseña `1234` guardada como hash).
- `onUpgrade` se ejecuta si algún día se sube el número `VERSION`. Por
  ahora borra las tablas y las vuelve a crear (suficiente para el curso;
  en una app real habría que migrar los datos).
- `onConfigure` activa las **claves foráneas** (`setForeignKeyConstraintsEnabled(true)`).
  SQLite las trae apagadas por defecto. Gracias a esto funciona el
  `ON DELETE CASCADE`: **al borrar una cuenta, sus mensajes se borran
  solos**.
- Todas las consultas son **parametrizadas**: los datos del usuario van
  en `ContentValues` (para insertar o actualizar) o en `selectionArgs`
  (los `?` del WHERE). Nunca se pega texto del usuario dentro del SQL,
  así que no se puede hacer inyección SQL. Hay una prueba que lo
  comprueba con `' OR '1'='1`.
- Cada `Cursor` se cierra con `.use { }`, que lo cierra automáticamente
  aunque ocurra un error.
- Los correos se guardan en minúscula y sin espacios, para que
  `Camila@...` y `camila@...` sean el mismo usuario (antes se usaba
  `ignoreCase`).
- `FerreSenasDbHelper.obtener(contexto)` entrega siempre la misma
  instancia, para no abrir varias conexiones a la vez.
- Si se crea con `nombreBd = null`, la base vive solo en memoria. Eso se
  usa en las pruebas.

El **catálogo de productos sigue fijo en código** (`CatalogoProductos`).
La tabla de mensajes solo guarda el `id` del producto.

### Archivo nuevo: `data/Seguridad.kt`

**Por qué:** la rúbrica pide no guardar la contraseña en texto plano.

**Cómo funciona:** usa **PBKDF2**, que ya viene en Android y en Java
(`javax.crypto`), así que tampoco necesita librerías.

- `hashContrasena("1234")` genera una **sal** aleatoria (16 bytes) y
  calcula el hash con 10.000 repeticiones. Se guarda como
  `sal:hash` en hexadecimal.
- `verificarContrasena("1234", guardado)` vuelve a calcular el hash con
  la misma sal y compara.
- La sal hace que dos usuarios con la misma contraseña tengan hashes
  distintos.
- Se usa `PBKDF2WithHmacSHA1` porque la versión con SHA256 recién existe
  desde Android 8 y la app parte en Android 7 (minSdk 24).

### Archivos modificados: `data/Usuario.kt` y `data/Producto.kt`

- `Usuario.contrasena` pasó a llamarse `hashContrasena`.
- `RepositorioUsuarios` y `RepositorioMensajes` dejaron de ser listas en
  memoria. Ahora son clases que reciben el `FerreSenasDbHelper` y llaman
  a sus métodos dentro de `withContext(Dispatchers.IO)`, o sea, en un
  hilo de fondo.
- `MensajeHistorial` ahora tiene un `id`, para poder editar o borrar
  ese mensaje.
- `SesionActual` se mantiene: guarda en memoria quién tiene la sesión
  abierta. Al cerrar sesión también borra el "Recordarme".

---

## 2. Integración front end + backend (ViewModel)

### Archivos nuevos: `ui/viewmodel/SesionViewModel.kt` y `ui/viewmodel/MensajesViewModel.kt`

**Por qué:** las pantallas no deberían hablar directo con la base de
datos, y las consultas no pueden correr en el hilo principal porque la
app se trabaría.

**Cómo funciona:**

- Las pantallas obtienen su ViewModel con `viewModel()` y llaman a
  funciones como `iniciarSesion(...)`, `guardar(...)` o `eliminar(...)`.
- El ViewModel lanza una **corrutina** con `viewModelScope.launch`. El
  repositorio hace la consulta en `Dispatchers.IO` y el resultado vuelve
  a la pantalla por un callback (`onExito` / `onError`).
- `SesionViewModel`: login, registro, recuperar contraseña, actualizar
  perfil, eliminar cuenta, cerrar sesión y restaurar la sesión guardada.
  Tiene un estado `cargando` para deshabilitar el botón mientras
  consulta.
- `MensajesViewModel`: tiene la lista `historial`. **Después de cada
  cambio** (guardar, editar, borrar, vaciar) vuelve a leer la lista desde
  la base, así la pantalla siempre muestra lo que está guardado.
- El historial siempre se filtra por el correo de `SesionActual`, así
  que **cada usuario ve solo sus mensajes**.

### Pantallas modificadas

| Archivo | Cambio |
|---|---|
| `LoginScreen.kt` | Usa `SesionViewModel.iniciarSesion`. El checkbox "Recordarme" ahora sí funciona, y toda la fila es tocable |
| `RegistroScreen.kt` | Usa `SesionViewModel.registrar`. Se mantienen `primerError` y `ejecutarSi`. Pide mínimo 4 caracteres de contraseña |
| `RecuperarContrasenaScreen.kt` | Ahora permite escribir y confirmar una contraseña nueva |
| `ConstructorMensajeScreen.kt` y `MensajePersonalizadoScreen.kt` | Guardan el mensaje con `MensajesViewModel.guardar` |
| `HistorialScreen.kt` | Lee de `MensajesViewModel` y agrega editar, borrar y vaciar |
| `PerfilScreen.kt` | Lee y guarda con los ViewModel. Agrega editar nombre y eliminar cuenta |
| `navigation/NavGraph.kt` | Al abrir la app revisa si hay sesión recordada y parte en Home o en Login |
| `MainActivity.kt` | Inicializa `SesionGuardada` |

---

## 3. Autenticación y sesión con SharedPreferences

### Archivo nuevo: `data/SesionGuardada.kt`

**Cómo funciona el "Recordarme":**

1. Al iniciar sesión con el checkbox marcado, se guarda **solo el correo**
   en SharedPreferences (archivo `ferresenas_sesion`). La contraseña
   no se guarda.
2. Al abrir la app, `NavGraph` muestra un círculo de carga mientras
   `SesionViewModel.restaurarSesion` busca ese correo en la base. Si lo
   encuentra, la app parte en el **Home**. Si la cuenta ya no existe,
   olvida el correo y parte en el Login.
3. **Cerrar sesión** (o eliminar la cuenta) borra el correo guardado.
4. Si no se marca "Recordarme", la sesión dura solo mientras la app está
   abierta.

**Login y registro:** se validan contra la tabla `usuarios`. Al registrar,
si el correo ya existe el `insert` falla por la clave primaria y se
muestra "Ese correo ya está registrado".

**Recuperar contraseña:** se ingresa el correo y la contraseña nueva dos
veces. Si el correo existe, se guarda el hash nuevo. (En una app real
primero habría que verificar la identidad con un código enviado al
correo.)

---

## 4. CRUD completo

| Operación | Dónde se hace en la app | Método de la base |
|---|---|---|
| **Crear** usuario | Registro | `insertarUsuario` |
| **Crear** mensaje | Constructor / Mensaje personalizado | `insertarMensaje` |
| **Consultar** historial | Pestaña Historial | `mensajesDeUsuario` |
| **Consultar** perfil | Login, Perfil | `buscarUsuario` |
| **Modificar** perfil | Perfil: nombre, preferencia, notificaciones | `actualizarUsuario` |
| **Modificar** contraseña | Recuperar contraseña | `actualizarHashContrasena` |
| **Modificar** mensaje | Historial → Editar | `actualizarTextoMensaje` |
| **Eliminar** mensaje | Historial → Borrar | `eliminarMensaje` |
| **Eliminar** historial | Historial → Vaciar historial | `eliminarMensajesDeUsuario` |
| **Eliminar** cuenta | Perfil → Eliminar mi cuenta | `eliminarUsuario` (+ cascada) |

### Archivo nuevo: `ui/screens/Dialogos.kt`

- `DialogoConfirmacion`: se usa antes de borrar un mensaje, vaciar el
  historial o eliminar la cuenta. El botón de confirmar va en color de
  error.
- `DialogoEditarTexto`: para editar un mensaje o el nombre. No deja
  guardar un texto vacío.
- **Alto contraste:** el `AlertDialog` normal usa un fondo gris que no
  está en el esquema de alto contraste. Por eso se le pone el fondo del
  tema (negro) y un borde amarillo cuando el alto contraste está activo.
- **Tamaño de letra:** los textos usan `MaterialTheme.typography`, que ya
  viene escalada según el tamaño elegido en Ajustes.

---

## 5. Pruebas

### Pruebas unitarias (`app/src/test`): 21 pruebas, todas en verde

| Archivo | Qué prueba |
|---|---|
| `UtilidadesTest.kt` | `textoACantidadSegura` (número válido, espacios, vacío, letras, cero, negativo, que el `finally` siempre corra), `primerError` (todo ok, devuelve el primero, no arma mensajes de más), `categoriasDisponibles` |
| `ConstruirMensajeTest.kt` | Las dos plantillas, cantidad vacía → 1, producto en minúscula, que no queden `{...}` sin reemplazar |
| `SeguridadTest.kt` | El hash no contiene la contraseña, verifica la correcta y rechaza la incorrecta, misma contraseña da hashes distintos, hash mal formado no cae la app |

Comando: `gradlew.bat testDebugUnitTest`

### Pruebas instrumentadas (`app/src/androidTest`): 14 pruebas, todas en verde en el emulador Pixel_6_Pro

| Archivo | Qué prueba |
|---|---|
| `FerreSenasDbHelperTest.kt` | Con una base **en memoria** (`nombreBd = null`): precarga de los 5 usuarios, crear/consultar/modificar usuario, correo repetido, mayúsculas, inyección SQL, cambiar contraseña, crear/listar/editar/borrar/vaciar mensajes, clave foránea, borrado en cascada |
| `LoginScreenTest.kt` | Prueba de UI con Compose: credenciales correctas entran, contraseña incorrecta muestra el error |

Comando (con emulador encendido): `gradlew.bat connectedDebugAndroidTest`

> Se subió `espresso-core` de 3.5.1 a 3.7.0 y `ext-junit` de 1.1.5 a
> 1.3.0: con las versiones viejas, las pruebas de UI se caían en Android
> nuevo con `NoSuchMethodException: InputManager.getInstance`.

---

## 6. APK y firma

### Archivos modificados: `app/build.gradle.kts` y `.gitignore`. Archivo nuevo: `keystore.properties.example`

- `versionCode` subió de 1 a **2** y `versionName` de "1.0" a **"2.0"**.
- Se agregó un `signingConfig` de **release** que lee la ruta del
  keystore, el alias y las contraseñas desde `keystore.properties`. **Las
  claves no están escritas en `build.gradle.kts`.**
- Si `keystore.properties` no existe, el proyecto igual compila y el
  release sale sin firmar (`app-release-unsigned.apk`).
- `.gitignore` ahora ignora `keystore.properties`, `*.jks` y `*.keystore`,
  así **el keystore y sus claves nunca se suben** a GitHub.
- `keystore.properties.example` es una plantilla con valores de ejemplo.

### Pasos para generar el APK firmado (en PowerShell, dentro de la carpeta `FerreSenas`)

`keytool` no está en tu PATH, por eso va con la ruta completa del JDK:

```powershell
# 1) crear el keystore (pide contraseña y tus datos; guarda bien la contraseña)
& "C:\Program Files\Java\jdk-25.0.2\bin\keytool.exe" -genkeypair -v `
  -keystore ferresenas-release.jks -storetype PKCS12 `
  -alias ferresenas -keyalg RSA -keysize 2048 -validity 10000

# 2) crear keystore.properties a partir de la plantilla y editarlo
Copy-Item keystore.properties.example keystore.properties
notepad keystore.properties
#    con PKCS12, storePassword y keyPassword son la misma contraseña

# 3) generar el APK firmado
.\gradlew.bat assembleRelease
#    queda en app\build\outputs\apk\release\app-release.apk

# 4) verificar la firma
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --verbose --print-certs `
  app\build\outputs\apk\release\app-release.apk
```

Si la firma está bien, `apksigner` muestra `Verifies` y
`Verified using v2 scheme (APK Signature Scheme v2): true`. Que el
esquema v1 aparezca en `false` es normal: con minSdk 24 Android usa v2.

**Importante:** guarda una copia del `.jks` y de la contraseña fuera del
proyecto (por ejemplo en un pendrive). Si los pierdes, no podrás publicar
actualizaciones firmadas con la misma clave.

---

## 7. Respuesta a la retroalimentación de la Semana 5

| Observación del profesor | Qué se hizo |
|---|---|
| Usuarios, catálogo y mensajes en memoria; se pierden al cerrar la app | Usuarios y mensajes ahora se guardan en SQLite (sección 1). El catálogo se dejó en código a propósito (ver abajo) |
| Fortalecer la separación de responsabilidades | La app quedó en capas: pantallas → ViewModel → repositorio → `FerreSenasDbHelper`. Las pantallas ya no tocan los datos directamente (sección 2) |
| Faltan texto a voz y voz a texto | Texto a voz ya existía (`LectorDeVoz.kt`, botón "Escuchar mensaje"). Se agregó **voz a texto** (abajo) |
| Usar try/catch donde realmente pueda fallar, con errores específicos | Se agregó try/catch con `SQLiteException` en las operaciones de base y con `ActivityNotFoundException` en el reconocimiento de voz. Se quitaron dos try/catch que no eran necesarios (abajo) |

### ¿Por qué el catálogo sigue en código?

El catálogo es información **fija y de solo lectura**: el usuario no crea,
edita ni borra productos. Lo que se pierde al cerrar la app, y había
que persistir, es lo que genera el usuario: su cuenta y sus mensajes.
La tabla `mensajes` guarda el `id` del producto y se cruza con el
catálogo al leer. Si en el futuro la ferretería pudiera administrar
sus productos, ahí sí convendría una tabla `productos`.

### Voz a texto: archivo nuevo `ui/screens/RespuestaVendedor.kt`

**Para qué sirve:** la persona sorda le muestra el mensaje al vendedor, y
el vendedor **responde hablando**. La app muestra esa respuesta **escrita
en grande**, así la comunicación funciona en los dos sentidos.

**Cómo funciona:**

- En la pantalla completa del mensaje hay un botón nuevo: **"Que el
  vendedor responda hablando"**.
- El botón abre el reconocedor de voz que trae Android
  (`RecognizerIntent.ACTION_RECOGNIZE_SPEECH`) en español de Chile. No
  necesita librerías ni pedir permiso de micrófono, porque el micrófono
  lo maneja la app de voz del sistema.
- La respuesta llega con `rememberLauncherForActivityResult` y se muestra
  en una tarjeta con el texto en grande, respetando el tamaño de letra
  elegido en Ajustes. Además el celular vibra para avisar que llegó.
- Si no se entendió o el vendedor canceló, se muestra un aviso para
  intentarlo de nuevo.

### Manejo de errores: archivos `SesionViewModel.kt`, `MensajesViewModel.kt`, `ConstructorMensajeScreen.kt` y `MensajePersonalizadoScreen.kt`

Siguiendo la recomendación, el try/catch se usa **solo donde algo puede
fallar de verdad** y con la excepción específica:

| Dónde | Qué puede fallar | Excepción |
|---|---|---|
| `textoACantidadSegura()` (se mantiene igual) | Convertir el texto de cantidad a número | `NumberFormatException` / `IllegalArgumentException` |
| `SesionViewModel` (login, registro, recuperar, perfil, eliminar cuenta) | La base de datos (disco lleno, base bloqueada, etc.) | `SQLiteException` |
| `MensajesViewModel.ejecutarEnBase()` | Lo mismo, para todas las operaciones del historial | `SQLiteException` |
| `RespuestaVendedor.kt` | Que el celular no tenga app de reconocimiento de voz | `ActivityNotFoundException` |

- En `SesionViewModel` el `finally` vuelve a habilitar el botón
  (`cargando = false`) tanto si la consulta funcionó como si falló.
- `ejecutarEnBase` es una **función de orden superior**: recibe la
  operación como lambda y le pone el try/catch alrededor, así no se
  repite el mismo bloque en guardar, editar, borrar, vaciar y cargar.
- El error se le muestra al usuario con un Toast (`AvisoDeError` en
  `Dialogos.kt`) en vez de que la app se cierre.

**try/catch que se quitaron porque no correspondían:**

- `ConstructorMensajeScreen`: tenía un try/catch alrededor de
  `textoACantidadSegura`, que ya devuelve un `Result` con su propio
  try/catch/finally adentro. Ahora usa directamente
  `.onSuccess { }` / `.onFailure { }`.
- `MensajePersonalizadoScreen`: usaba `require` + `catch` solo para
  revisar que el texto no estuviera vacío. Es una validación normal, así
  que ahora es un `if`.

---

## 8. Capturas de pantalla sugeridas para el informe

**Persistencia y sesión**
1. Login con el checkbox "Recordarme" marcado.
2. Home después de cerrar la app por completo y volver a abrirla (entra
   directo, sin pasar por el Login).
3. Historial con mensajes que siguen ahí después de cerrar y abrir la app.
4. Registro con el error "Ese correo ya está registrado".
5. Recuperar contraseña con los campos de contraseña nueva, y luego un
   login exitoso con esa contraseña.
6. (Opcional) Android Studio → **App Inspection → Database Inspector**
   mostrando las tablas `usuarios` (con la columna `hash_contrasena`) y
   `mensajes`.

**CRUD**
7. Historial con los botones Editar / Borrar y "Vaciar historial".
8. Diálogo "Editar mensaje" y el mensaje ya editado en la tabla.
9. Diálogo "¿Borrar este mensaje?".
10. Diálogo "¿Vaciar el historial?".
11. Perfil con "Editar nombre" y el diálogo abierto.
12. Diálogo "¿Eliminar tu cuenta?" y luego el Login tras eliminarla.
13. Un diálogo de confirmación con **alto contraste** activado y letra
    "Muy grande".

**Pruebas**
14. Resultado de `gradlew.bat testDebugUnitTest` (BUILD SUCCESSFUL) o el
    reporte `app/build/reports/tests/testDebugUnitTest/index.html`.
15. Reporte de pruebas instrumentadas
    `app/build/reports/androidTests/connected/debug/index.html` (14/14).

**APK y firma**
16. `build.gradle.kts` con `versionCode = 2`, `versionName = "2.0"` y el
    bloque `signingConfigs` (sin claves a la vista).
17. `.gitignore` con `keystore.properties` y `*.jks`.
18. Salida de `apksigner verify --verbose --print-certs` con `Verifies`.
19. El archivo `app-release.apk` en la carpeta de salida.

**Retroalimentación S5**
20. Pantalla completa del mensaje con los botones "Escuchar mensaje" y
    "Que el vendedor responda hablando".
21. La ventana del reconocedor de voz abierta (hay que probarlo en un
    celular real o en un emulador con micrófono).
22. La tarjeta "El vendedor dijo: …" con la respuesta escrita.
23. Fragmento de `SesionViewModel.kt` con el `try / catch (e: SQLiteException) / finally`.

**Git**
24. `git log --oneline` mostrando un commit por etapa.
