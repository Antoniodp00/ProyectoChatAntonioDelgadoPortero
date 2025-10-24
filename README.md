# Proyecto: Chat Offline XML (JavaFX)

## Descripción General

**Chat Offline XML** es una aplicación de escritorio desarrollada con **JavaFX** que permite la comunicación mediante chat entre usuarios registrados en un entorno local, sin necesidad de conexión a internet. La persistencia de los datos de usuario y las conversaciones se realiza localmente utilizando **archivos XML**.

La aplicación incluye gestión de **archivos adjuntos** (con validaciones de tamaño y tipo), múltiples opciones de **exportación** (TXT, CSV, ZIP) y **análisis estadístico** básico de las conversaciones mediante la API de Streams de Java. La interfaz presenta los mensajes en un formato moderno de **burbujas de chat**.

---

## Características Principales

* **Gestión de Usuarios:** Registro y autenticación segura (hashing con bcrypt).
* **Chat Individual:** Comunicación uno a uno entre usuarios registrados.
* **Persistencia XML:** Usuarios (`usuarios.xml`) y Mensajes (`mensajes.xml`) guardados localmente usando JAXB.
* **Archivos Adjuntos:**
    * Envío de imágenes, PDF, documentos, etc.
    * Validación de tamaño (máx. 10MB) y tipo.
    * Almacenamiento local en carpeta `media/`.
    * Visualización (miniaturas para imágenes) y enlaces para abrir/exportar.
* **Exportación:**
    * Conversación a **TXT**.
    * Conversación a **CSV**.
    * Conversación completa (texto + adjuntos) a **ZIP**.
    * Adjuntos individuales.
* **Estadísticas:** Resumen de la conversación (total mensajes, mensajes por usuario, palabras más comunes) usando **Java Streams**.
* **Interfaz Moderna:** Uso de JavaFX, FXML para estructura, CSS para estilo, y `ListView` con celdas personalizadas (`ChatMessageCell`) para burbujas de chat. Iconos con ControlsFX.

---

## Tecnologías Utilizadas

* **Lenguaje:** Java 21+
* **Interfaz Gráfica:** JavaFX, FXML, CSS
* **Gestión de Proyecto/Dependencias:** Maven
* **Persistencia:** JAXB (para XML)
* **Seguridad:** jBCrypt (hashing de contraseñas)
* **Logging:** SLF4J + Logback
* **Utilidades:** Java Streams API, ControlsFX (iconos).

---

## Requisitos

* **JDK (Java Development Kit):** Versión 21 o superior.
* **Apache Maven:** Versión 3.8 o superior.
* **Sistema Operativo:** Compatible con JavaFX (Windows, macOS, Linux).

---

## Instalación y Ejecución

1.  **Clonar/Descargar:** Obtén el código fuente del proyecto.
2.  **Compilar:** Abre una terminal en el directorio raíz del proyecto (donde está `pom.xml`) y ejecuta:
    ```bash
    mvn clean package
    ```
    *(Nota: Puedes usar `mvn -q -e -DskipTests clean package` para una compilación más silenciosa y rápida si no necesitas ejecutar tests)*.
3.  **Ejecutar (Recomendado):** En la misma terminal, ejecuta:
    ```bash
    mvn clean javafx:run
    ```
    Esto utiliza el plugin de JavaFX configurado en Maven.
4.  **Ejecutar (Alternativa - IDE):** Importa el proyecto como un proyecto Maven en tu IDE (IntelliJ, Eclipse, etc.) y ejecuta la clase `main` de `com.dam.adp.proyectochatantoniodelgadoportero.app.Launcher`.

**Importante:** La aplicación necesita permisos de escritura en su directorio de ejecución para crear/modificar las carpetas `data/` (para los XML) y `media/` (para los adjuntos).

---

## Versión Online (Opcional)

Este repositorio también contiene una versión experimental del chat que utiliza **Sockets** para comunicación en red (cliente-servidor). Esta versión se encuentra en una rama separada llamada `Version-online`.

**Para acceder y probar la versión online:**

1.  Asegúrate de tener Git instalado.
2.  Abre una terminal en el directorio raíz de tu copia local del proyecto.
3.  **Actualiza tu repositorio local** para asegurarte de tener la información de todas las ramas remotas:
    ```bash
    git fetch origin
    ```
4.  **Cambia a la rama** `Version-online`:
    ```bash
    git checkout Version-online
    ```
    o si prefieres crear una rama local basada en la remota:
    ```bash
    git checkout -b mi-version-online origin/Version-online
    ```

Una vez en la rama `Version-online`, encontrarás las clases `ChatServer`, `ClientHandler` y `ChatClient` (o similares) que implementan la lógica de red. *Recuerda que esta versión puede ser experimental y no estar tan pulida como la versión offline principal.*

---

## Estructura del Proyecto

El proyecto sigue una arquitectura basada en **MVC (Modelo-Vista-Controlador)**:

* `src/main/java/com/dam/adp/proyectochatantoniodelgadoportero`:
    * `app/`: Clases de arranque de JavaFX (`Launcher`, `Aplicacion`).
    * `controller/`: Controladores JavaFX (`MainController`, `InicioSesionController`, etc.).
    * `model/`: Clases de dominio (`Usuario`, `Mensaje`, `Sesion`, etc.).
    * `DAO/`: Clases de Acceso a Datos (`UsuarioDAO`, `MensajeDAO`) para interactuar con los XML.
    * `utils/`: Clases de utilidad (`FileManager`, `XMLManager`, `PasswordManager`, `StreamUtils`, `ChatMessageCell`, etc.).
* `src/main/resources/com/dam/adp/proyectochatantoniodelgadoportero`:
    * `*.fxml`: Archivos FXML definiendo las vistas.
    * `style.css`: Hoja de estilos CSS.
* `data/`: Directorio donde se almacenan `usuarios.xml` y `mensajes.xml`.
* `media/`: Directorio donde se guardan las copias de los archivos adjuntos.

---

## Persistencia

* **Usuarios y Mensajes:** Se almacenan en formato **XML** en los archivos `data/usuarios.xml` y `data/mensajes.xml` respectivamente. La serialización/deserialización se gestiona mediante **JAXB**.
* **Adjuntos:** Los archivos adjuntos **se copian** a la carpeta `media/` con un nombre único (UUID) para evitar colisiones. En el archivo `mensajes.xml` se guarda el nombre original y la ruta relativa del archivo copiado.

---

## Logging

La aplicación utiliza **SLF4J** como fachada de logging y **Logback** como implementación. Los eventos y errores se registran tanto en la consola como en el archivo `chat.log` ubicado en el directorio raíz del proyecto. La configuración se encuentra en `src/main/resources/logback.xml`.

---

## Futuras Mejoras (Sugerencias)

* **Rendimiento:** Migrar la persistencia a una base de datos embebida (SQLite, H2) para mejor escalabilidad.
* **Concurrencia:** Usar `Task` o `Service` de JavaFX para operaciones I/O en hilos de fondo.
* **Funcionalidades:** Añadir chat grupal, notificaciones, edición/eliminación de mensajes, búsqueda.
* **UX/UI:** Añadir avatares, formato de texto, mejores previsualizaciones.
* **Calidad:** Implementar pruebas unitarias y de integración.

---

## Autor

* **Antonio Delgado Portero** (Proyecto DAM 2025-2026)
