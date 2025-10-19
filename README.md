# Proyecto Chat Offline XML (JavaFX)

Aplicación de chat local construida con JavaFX que persiste usuarios y mensajes en ficheros XML. Incluye gestión de adjuntos (imágenes y documentos) y múltiples opciones de exportación de conversaciones.


## Índice
- Descripción general
- Requisitos
- Compilación y ejecución
- Estructura del proyecto
- Uso de la aplicación
  - Inicio y autenticación
  - Pantalla principal (chat)
  - Adjuntar archivos (añadir, validar, quitar, abrir, exportar)
  - Exportar conversación (TXT, CSV, ZIP)
  - Resumen/estadísticas de la conversación
- Persistencia y rutas de archivos
- Registro (logging)
- Solución de problemas


## Descripción general
Este proyecto implementa un chat “offline” entre usuarios registrados. Los datos se guardan en XML (usuarios y mensajes). La interfaz está realizada en JavaFX y los estilos se gestionan con CSS. Se ha aplicado un diseño minimalista para una experiencia moderna y clara.

Características clave:
- Gestión de usuarios y sesiones.
- Chat entre dos usuarios con mensajes persistidos en XML.
- Adjuntar archivos a los mensajes con validaciones.
- Exportar conversaciones a TXT o CSV.
- Exportar adjuntos individualmente.
- Generar ZIP con conversación y adjuntos.
- Generación de estadísticas de la conversación.


## Requisitos
- Java 21 (o compatible con las dependencias de JavaFX 21).
- Maven 3.8+.
- Sistema operativo con soporte para JavaFX (Windows, macOS, Linux).


## Compilación y ejecución
1) Compilación:
- mvn -q -e -DskipTests clean package

2) Ejecución (recomendado):
- mvn clean javafx:run

3) Alternativa (IDE/CLI):
- Ejecutar la clase principal com.dam.adp.proyectochatantoniodelgadoportero.app.Launcher.

Nota: El plugin javafx-maven-plugin está añadido en el pom.xml. Si tu IDE no resuelve bien el mainClass configurado por defecto del plugin, ejecuta la clase Launcher directamente.


## Estructura del proyecto
- src/main/java/com/dam/adp/proyectochatantoniodelgadoportero
  - app/Aplicacion.java y app/Launcher.java: arranque JavaFX.
  - controller/MainController.java: lógica de la vista principal (chat, exportaciones y adjuntos).
  - controller/InicioSesionController.java, RegistroController.java, LandingPageController.java.
  - utils/FileManager.java: operaciones de archivos (guardar/abrir/exportar, ZIP, validaciones, MIME).
  - utils/XMLManager.java: lectura/escritura de XML con JAXB.
  - DAO/*: acceso a datos guardados en XML.
  - model/*: clases de dominio (Usuario, Mensaje, etc.).
- src/main/resources/com/dam/adp/proyectochatantoniodelgadoportero
  - *.fxml: vistas JavaFX.
  - style.css: estilos (incluye minimalista overrides).
- data: ficheros XML de datos (usuarios.xml, mensajes.xml).
- media: carpeta interna donde se guardan copias de adjuntos.


## Uso de la aplicación
### Inicio y autenticación
- Desde la pantalla de inicio (landing) podrás Iniciar Sesión o Registrarte.
- Tras iniciar sesión, accedes a la pantalla principal del chat.

### Pantalla principal (chat)
Elementos principales (ver mainView.fxml):
- Lista de usuarios a la izquierda.
- Área de conversación al centro.
- Caja de texto para escribir mensajes y botones: Adjuntar, Enviar.
- Panel de adjuntos con opciones: Quitar, Abrir, Exportar.
- Barra inferior con exportaciones: Exportar TXT, Exportar CSV, ZIP (conv+adj).
- Etiquetas de estado y estadísticas.


### Adjuntar archivos
Botón: “Adjuntar” (en la zona de envío del mensaje).
- Formatos permitidos: .png, .jpg, .jpeg, .gif, .pdf, .txt, .docx, .xlsx
- Tamaño máximo: 10 MB por archivo.
- Validaciones: se comprueba existencia, tamaño y extensión (ver FileManager.validarArchivo).
- Selección: al elegir un archivo se muestra en la lista de adjuntos (previo a enviar).
- Quitar: elimina el adjunto seleccionado antes de enviar.
- Abrir: intenta abrir el adjunto con la app predeterminada del sistema (FileManager.abrirArchivo). Requiere que el archivo exista en local.

Envío del mensaje con adjunto:
- Cuando envías el mensaje, la aplicación guarda una copia del adjunto en la carpeta interna media/ con un nombre único (FileManager.generarNombreUnico + FileManager.guardarArchivo).
- En los datos del mensaje se almacena el nombre del adjunto y su ruta relativa.

Exportar adjunto individual:
- Usa el botón “Exportar” del panel de adjuntos.
- Se elige una CARPETA de destino y se copia el archivo allí (FileManager.exportarArchivo).


### Exportar conversación
Opciones disponibles en la barra inferior de la pantalla principal:

1) Exportar TXT
- Genera un archivo .txt con el formato: [yyyy-MM-dd HH:mm:ss] remitente: contenido
- Limpia saltos de línea en el contenido para mantener una línea por mensaje.
- Implementación: MainController.exportarTxt → FileManager.exportarAArchivoTexto.

2) Exportar CSV
- Genera un .csv con cabecera FechaHora;Remitente;Contenido
- Usa ; como separador y elimina separadores y saltos de línea del contenido.
- Implementación: MainController.exportarCsv → FileManager.exportarAArchivoCsv.

3) ZIP (conv+adj)
- Empaqueta la conversación y los adjuntos en un único ZIP.
- Implementación base en FileManager.crearArchivoZip (invocado desde MainController.exportarZip, si está conectado en la vista). El ZIP suele incluir:
  - un TXT con la conversación formateada
  - las copias de adjuntos almacenadas en media/


### Resumen/estadísticas de la conversación
Botón: “Generar resumen”.
- Calcula total de mensajes, recuento por usuario y top de palabras más comunes.
- Permite guardarlo como TXT (FileManager.exportarEstadisticas).


## Persistencia y rutas de archivos
- Datos persistidos en XML en la carpeta data/ (usuarios.xml, mensajes.xml). Gestión mediante XMLManager (JAXB).
- Adjuntos: se copian a la carpeta media/ con nombres únicos. Se guardan rutas relativas en los mensajes para poder recuperarlos.
- Exportaciones: el usuario elige dónde guardar los archivos (TXT/CSV/ZIP) mediante selectores del sistema.


## Registro (logging)
- Configurado con SLF4J + Logback (ver src/main/resources/logback.xml y src/main/java/.../log/logback.xml si aplica).
- Se genera salida de logs y un chat.log en la raíz del proyecto (según configuración).


## Solución de problemas
- No se abre un adjunto: verifica que el archivo existe en media/ o en la ruta de origen, y que el sistema tiene aplicación asociada (Desktop.isDesktopSupported).
- Error al exportar: comprueba permisos de escritura en la carpeta de destino y que haya mensajes/adjuntos seleccionados.
- No aparecen usuarios/mensajes: revisa los XML en data/. Si están vacíos o corruptos, XMLManager crea archivos y puede reescribir estructuras mínimas.
- Problemas con la ejecución Maven/JavaFX: ejecuta la clase Launcher desde el IDE, o asegura que JAVA_HOME apunta a un JDK compatible con JavaFX 21.

---
Autor: Antonio Delgado Portero (Proyecto DAM)
Fecha: 2025-10-19
