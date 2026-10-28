# TicketHN para Android

Aplicación para explorar eventos en Honduras y gestionar boletos, desarrollada como proyecto integrador de Ingeniería en Computación de la **Universidad Tecnológica de Honduras (UTH)**, para la asignatura Programación Móvil I.

Aplicación nativa en Java con interfaces XML. Actualmente funciona con datos mock y no requiere un backend para ejecutarse.

## Estructura

- `app/`: código, recursos y pruebas de la aplicación.
- `gradle/`: catálogo de versiones, Gradle Wrapper y configuración de la JVM.
- `maps.properties.example`: plantilla para configurar Google Maps localmente.

## Funciones disponibles

- **Home:** búsqueda por nombre, recinto o ciudad; filtros por categoría y ciudad; carrusel de hasta tres eventos con avance automático cada cinco segundos, navegación manual y recorrido circular.
- **Detalle del evento:** imagen y título fijos, localidades, disponibilidad mock, selección de cantidad y cálculo del total. Incluye compartir, mapa con desplazamiento y zoom, y acceso externo a Google Maps.
- **Mis Boletos:** tres boletos mock con paginación horizontal que centra cada ticket e indicador de posición.
- **Perfil:** inicio de sesión a pantalla completa, validación de correo y contraseña no vacía, y regreso al Home al simular un acceso correcto.
- **Términos y condiciones:** pantalla independiente con contenido desplazable.
- **Acerca de:** descripción del proyecto, integrantes y botón de contacto.

### Alcance del prototipo

Los precios, inventarios, órdenes y boletos son ejemplos. Comprar boletos muestra un resumen; no realiza cobros, reservas ni emite entradas.

Los patrones similares a QR son dibujos de demostración: no contienen enlaces, no son códigos funcionales y no permiten entrar a eventos.

El inicio de sesión acepta un correo con formato válido y una contraseña no vacía, sin consultar una base de datos. No crea una sesión real ni guarda la contraseña. Google y recuperación de contraseña muestran mensajes informativos; aún no están conectados a servicios externos.

El mapa integrado usa Google Maps cuando se configura una clave válida. Los marcadores utilizan coordenadas de referencia de la ciudad; el botón **Abrir en Google Maps** busca el recinto por nombre.

## Tecnologías y requisitos

Configuración actual de la aplicación:

| Elemento | Versión / configuración |
| --- | --- |
| Lenguaje | Java, compatibilidad de código Java 11 |
| Interfaz | XML, Material Components y AndroidX |
| Android mínimo | Android 7.0, API 24 |
| SDK de compilación y destino | API 37 |
| Android Gradle Plugin | 9.4.1 |
| Gradle Wrapper | 9.6.0 |
| JVM del daemon de Gradle | JDK 25 |
| Paginación | ViewPager2 1.0.0 |
| Mapas | Google Maps SDK for Android 20.1.0 |

Necesitas Android Studio compatible con la configuración anterior, el SDK de Android API 37 y un teléfono o emulador con API 24 o superior. Para probar el mapa integrado, usa un dispositivo con Google Play services y conexión a Internet.

La versión de Java del código y la JVM que ejecuta Gradle son configuraciones distintas. El proyecto solicita JDK 25 en `gradle/gradle-daemon-jvm.properties` y tiene configurado un resolver de toolchains. La primera sincronización puede descargar Gradle, dependencias y la JVM requerida.

## Ejecutar la aplicación

1. Abre **ProgramacionMovil1_UTH_App** en Android Studio.
2. Espera a que termine la sincronización de Gradle y configura el SDK local si Android Studio lo solicita.
3. Configura Google Maps como se indica abajo si quieres usar el mapa integrado.
4. Selecciona el módulo **app** y un dispositivo o emulador.
5. Ejecuta la aplicación con **Run**.

El backend no es necesario para probar las pantallas actuales.

### Compilar desde PowerShell

Desde la raíz de este proyecto Android:

```powershell
.\gradlew.bat :app:assembleDebug
```

La APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

En macOS o Linux, usa `./gradlew` en lugar de `.\gradlew.bat`.

## Configurar Google Maps

La clave pertenece a tu proyecto de Google Cloud. No hay una clave compartida ni una clave de demostración válida incluida en el código.

1. Habilita **Maps SDK for Android** en tu proyecto de Google Cloud y configura la facturación requerida por Google Maps Platform.
2. Crea una clave y restringe su uso a Maps SDK for Android y a tu aplicación Android.
3. Utiliza el paquete `dev.josegaldamez.tickethn_uth` y el SHA-1 del certificado con el que compilas. Para consultar los certificados desde la carpeta Android:

   ```powershell
   .\gradlew.bat :app:signingReport
   ```

4. Si no existe, crea `maps.properties` junto al archivo `settings.gradle.kts`, a partir de [maps.properties.example](maps.properties.example):

   ```powershell
   if (-not (Test-Path .\maps.properties)) {
       Copy-Item .\maps.properties.example .\maps.properties
   }
   ```

5. Edita únicamente el archivo local `maps.properties`:

   ```properties
   MAPS_API_KEY=TU_CLAVE_DE_GOOGLE_MAPS
   ```

6. Vuelve a compilar e instalar la aplicación después de cambiar la clave.

`maps.properties`, `local.properties`, los directorios de compilación y los archivos APK/AAB están excluidos en el [.gitignore de Android](.gitignore). **No subas la clave al repositorio ni la escribas directamente en el manifiesto o los recursos.** El archivo de ejemplo solo debe contener un marcador de posición.

Gradle lee la propiedad local e incorpora la clave al manifiesto generado. Una clave de una aplicación Android puede extraerse de la APK; las restricciones por API, paquete y certificado siguen siendo necesarias.

Sin clave o con el marcador `DEMO_`, la pantalla permite abrir la ubicación externamente. Si el mapa no carga, aparece un mensaje en lugar de quedar en blanco. Ante un error de autorización, revisa la API habilitada, las restricciones, el certificado y la facturación.

Consulta la [configuración oficial de Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/config).

## Organización del código Android

El paquete principal es `dev.josegaldamez.tickethn_uth`, bajo `app/src/main/java/`.

| Paquete / archivo | Responsabilidad |
| --- | --- |
| `MainActivity.java` | Home y navegación principal |
| `home/` | Modelo de evento, repositorio mock, tarjetas y carrusel |
| `details/` | Detalle, localidades, selección y contenedor de gestos del mapa |
| `tickets/` | Modelo, repositorio mock, paginación y componentes visuales del boleto |
| `auth/` | Inicio de sesión simulado y términos |
| `about/` | Información de la aplicación y contacto |

Los layouts están en `app/src/main/res/layout/`; los textos, colores y estilos en `res/values/`; las imágenes locales en `res/drawable-nodpi/`.

Para cambiar los datos de ejemplo, edita `MockEventRepository`, `MockTicketRepository` o `MockEventDetailRepository`. La lógica de cantidades y totales está separada de la Activity en `TicketSelection`.

La organización actual usa Activities, modelos, repositorios mock y componentes reutilizables. **MVVM, la API REST en .NET con Clean Architecture, JWT/OAuth 2.0, el control de sobreventa, la validación offline y la lectura con CameraX forman parte del alcance previsto; todavía no están implementados.**

## Pruebas

Desde `ProgramacionMovil1_UTH_App`, ejecuta las pruebas específicas de selección de boletos:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests dev.josegaldamez.tickethn_uth.details.TicketSelectionTest
```

Estas pruebas verifican los límites de cantidad, el ajuste por disponibilidad al cambiar de localidad y el cálculo de totales, incluidos los paquetes de palco.

Los gestos, transiciones, teclado y presentación visual se comprueban en un teléfono o emulador. Las pruebas anteriores no validan compras reales ni servicios externos.

## Integrantes del Grupo #3

- Christian Yahir Rodriguez Sanchez
- William Eduardo Banegas Cruz
- Jose Oveniel Galdamez Ramos
- Linda Guadalupe Nolasco Martinez
- Melvin Edgardo Peña Caballero
- Ricardo Andres Montes Jovel

## Contacto

Comentarios y sugerencias: [josegaldamez.dev](https://josegaldamez.dev).


