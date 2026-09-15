# FerreSeñas

> ⚠️ **Proyecto en construcción.** Esta es una entrega académica en desarrollo activo, no una versión final ni un producto listo para producción.

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

## Funcionalidades actuales

- **Inicio de sesión** y **registro de usuario** con correo y contraseña.
- **Recuperación de contraseña**.
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
- **Historial** de los mensajes generados durante la sesión (por producto
  o personalizados).
- **Perfil** de usuario, con avatar personalizable (foto desde la
  galería), preferencia de comunicación y notificaciones editables, y
  cierre de sesión.
- **Configuración de accesibilidad**: alto contraste, tamaño de letra
  ajustable para toda la aplicación y vibración de confirmación al
  generar o guardar un mensaje. Las preferencias se guardan en el
  dispositivo y se mantienen al cerrar la app.
- Navegación con menú inferior (Inicio / Historial / Perfil / Ajustes) y
  barra superior con el nombre y correo del usuario.

## Tecnologías

- **Kotlin** + **Jetpack Compose** (UI 100% declarativa)
- **Material Design 3**
- **Navigation Compose** para la navegación entre pantallas
- **SharedPreferences** para las preferencias de accesibilidad
- **TextToSpeech** de Android para la lectura en voz alta
- **Android Gradle Plugin 9.3.0** / **Gradle 9.7.1**

### Funciones de Kotlin integradas

- **Funciones de orden superior e inline**: `ejecutarSi()` y
  `primerError()` (`Utilidades.kt`), usadas para validar el formulario de
  Registro sin repetir un bloque `when`.
- **Funciones lambda y operaciones con colecciones**: `filter`, `find`,
  `forEach`, `map`, `distinct` y `sortedBy` en el catálogo, los
  repositorios de usuarios/mensajes y las utilidades de la app.
- **Funciones y propiedades de extensión**: sobre `String`, `Int`,
  `Context`, `Usuario`, `List<Producto>` y `List<MensajeHistorial>`
  (`Utilidades.kt`, `Preferencias.kt`, `Accesibilidad.kt`, `Usuario.kt`).
- **Manejo de errores y excepciones**: `try/catch/finally` en
  `textoACantidadSegura()` para validar la cantidad ingresada sin que la
  app se detenga.

## Estado del proyecto / limitaciones conocidas

- Los datos de usuarios, catálogo y mensajes se manejan **en memoria**: no
  hay base de datos ni backend, por lo que se pierden al cerrar la app
  (las preferencias de accesibilidad sí se guardan en el dispositivo).
- No incluye todavía voz a texto (dictado); solo lectura en voz alta.
- Pensado y probado como avance de una actividad académica de la
  asignatura Desarrollo de Aplicaciones Móviles (Duoc UC); no representa
  el alcance final del producto.

## Cómo ejecutar el proyecto

1. Clonar este repositorio.
2. Abrir la carpeta del proyecto en **Android Studio** (versión Quail o
   superior, compatible con AGP 9.x).
3. Dejar que Gradle sincronice (puede descargar el SDK Platform 37 y
   Build-Tools 36.0.0 si no los tienes instalados).
4. Ejecutar en un emulador o dispositivo Android 7.0 (API 24) o superior.

No se necesita configuración adicional: no hay claves de API ni backend
externo que configurar.
