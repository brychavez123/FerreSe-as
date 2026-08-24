# FerreSeñas

> ⚠️ **Proyecto en construcción.** Esta es una entrega académica en desarrollo activo, no una versión final ni un producto listo para producción.

FerreSeñas es una aplicación móvil Android de accesibilidad, pensada para que
**personas con discapacidad sensorial auditiva** puedan comunicarse con
facilidad al comprar en una ferretería.

## ¿Qué problema resuelve?

Explicar verbalmente qué producto se necesita, en qué cantidad y de qué
medida no siempre es posible para una persona sorda o hipoacúsica, y no
todos los vendedores conocen lengua de señas. FerreSeñas resuelve esto
permitiendo armar un **mensaje visual claro** (por ejemplo: *"Necesito 10
tornillos de esta medida: 1/2\""* o *"¿Tiene disponible martillo en esta
medida: Grande?"*) que el cliente simplemente le muestra en pantalla al
vendedor.

## Funcionalidades actuales

- **Inicio de sesión** y **registro de usuario** con correo y contraseña.
- **Recuperación de contraseña**.
- **Catálogo de productos** de ferretería, con filtro rápido por categoría
  (Fijación, Pintura, Medición, Herramientas, Eléctrico) y fotos reales de
  cada producto.
- **Constructor de mensajes visuales**: selecciona cantidad, medida y el
  tipo de mensaje (compra o consulta de disponibilidad), y genera un texto
  grande y claro pensado para mostrar al vendedor.
- **Historial** de los mensajes generados durante la sesión.
- **Perfil** de usuario, con avatar personalizable (foto desde la galería)
  y cierre de sesión.
- Navegación con menú inferior (Inicio / Historial / Perfil) y barra
  superior con el nombre y correo del usuario.

## Tecnologías

- **Kotlin** + **Jetpack Compose** (UI 100% declarativa)
- **Material Design 3**
- **Navigation Compose** para la navegación entre pantallas
- **Android Gradle Plugin 9.3.0** / **Gradle 9.7.1**

## Estado del proyecto / limitaciones conocidas

- Los datos de usuarios, catálogo y mensajes se manejan **en memoria**: no
  hay base de datos ni backend, por lo que se pierden al cerrar la app.
- No incluye todavía funciones de texto a voz / voz a texto (planeadas para
  una siguiente iteración).
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
