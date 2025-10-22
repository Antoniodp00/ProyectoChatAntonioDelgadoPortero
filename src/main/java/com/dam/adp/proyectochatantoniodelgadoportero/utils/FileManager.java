package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;

import java.awt.*;
import java.io.*;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManager {

    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private static final String RUTAMEDIA = "media" + File.separator;
    private static final DateTimeFormatter EXPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final long TAMAÑO_MAXIMO_BYTES = 10L * 1024 * 1024; // 10 MB
    public static final List<String> EXTENSIONES_PERMITIDAS = Collections.unmodifiableList(
            Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".pdf", ".txt", ".docx", ".xlsx")
    );
    public static final String NOMBRE_TEXTO_CONVERSACION = "conversacion.txt";
        public static final String CSV_SEPARADOR = ";";

    /**
     * Constructor privado para evitar la instanciación de la clase de utilidad.
     */
    private FileManager() {}

    /**
     * Crea (si no existen) las carpetas internas de trabajo como 'media'.
     */
    public static void asegurarDirectorios() {
        new File(RUTAMEDIA).mkdirs();
    }

    /**
     * Exporta una lista de Strings (estadísticas) a un archivo TXT en la ubicación especificada.
     *
     * @param estadisticas La lista de líneas de estadísticas a escribir.
     * @param archivoDestino El objeto File que representa la RUTA COMPLETA elegida por el usuario.
     * @return true si la exportación fue exitosa, false en caso contrario.
     */
    public static boolean exportarEstadisticas(ArrayList<String> estadisticas, File archivoDestino) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDestino))) {
            if (estadisticas != null) {
                for (String estadistica : estadisticas) {
                    if (estadistica == null) {
                        continue;
                    }
                    bw.write(estadistica);
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            log.error("Error al exportar estadísticas a archivo {}", archivoDestino, e);
            return false;
        }
    }

    /**
     * Exporta una lista de mensajes a un archivo de texto plano (.txt) en la ubicación especificada.
     *
     * @param mensajes La lista de mensajes de la conversación.
     * @param archivoDestino El objeto File que representa la RUTA COMPLETA elegida por el usuario.
     * @return true si la exportación fue exitosa, false en caso contrario.
     */
    public static boolean exportarAArchivoTexto(List<Mensaje> mensajes, File archivoDestino) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDestino))) {
            List<Mensaje> lista = (mensajes != null) ? mensajes : java.util.Collections.emptyList();
            for (Mensaje mensaje : lista) {
                if (mensaje == null) {
                    continue;
                }

                String fechaHora = (mensaje.getFecha() != null) ? mensaje.getFecha().format(EXPORT_FORMATTER) : "----";
                String contenido = mensaje.getMensaje() == null ? "" : mensaje.getMensaje().replace('\n', ' ');
                String remitente = mensaje.getRemitente() == null ? "" : mensaje.getRemitente();

                String linea = String.format("[%s] %s: %s", fechaHora, remitente, contenido);

                bw.write(linea);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            log.error("Error al exportar conversación a TXT {}", archivoDestino, e);
            return false;
        }
    }

    /**
     * Exporta una lista de mensajes a un archivo de valores separados por comas (.csv) en la ubicación especificada.
     *
     * @param mensajes La lista de mensajes de la conversación.
     * @param archivoDestino El objeto File que representa la RUTA COMPLETA elegida por el usuario.
     * @return true si la exportación fue exitosa, false en caso contrario.
     */
    public static boolean exportarAArchivoCsv(List<Mensaje> mensajes, File archivoDestino) {
        final String SEPARADOR = CSV_SEPARADOR;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDestino))) {
            // Escribir la cabecera del CSV
            bw.write("FechaHora" + SEPARADOR + "Remitente" + SEPARADOR + "Contenido");
            bw.newLine();

            List<Mensaje> lista = (mensajes != null) ? mensajes : java.util.Collections.emptyList();
            for (Mensaje mensaje : lista) {
                if (mensaje == null) {
                    continue;
                }

                String fechaHora = (mensaje.getFecha() != null) ? mensaje.getFecha().format(EXPORT_FORMATTER) : "----";
                String remitente = mensaje.getRemitente() == null ? "" : mensaje.getRemitente();
                String contenido = mensaje.getMensaje() == null ? "" : mensaje.getMensaje();
                String contenidoLimpio = contenido.replace(SEPARADOR, "").replace('\n', ' ');

                String linea = fechaHora + SEPARADOR + remitente + SEPARADOR + contenidoLimpio;

                bw.write(linea);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            log.error("Error al exportar conversación a CSV {}", archivoDestino, e);
            return false;
        }
    }

    /**
         * Guarda una copia del archivo de origen dentro de la carpeta interna 'media' con el nombre indicado.
         * @param origen archivo a copiar.
         * @param nombreArchivo nombre de archivo destino (normalmente generado de forma única).
         * @return true si se copia con éxito; false en caso de error.
         */
        public static boolean guardarArchivo(File origen, String nombreArchivo) {
        asegurarDirectorios();
        File destino = new File(RUTAMEDIA + nombreArchivo);
        try (InputStream entrada = new BufferedInputStream(new FileInputStream(origen));
             OutputStream salida = new BufferedOutputStream(new FileOutputStream(destino))) {
            entrada.transferTo(salida);
            return true;
        } catch (IOException e) {
            log.error("Error al guardar archivo en 'media': origen={}, destino={} ", origen, destino.getPath(), e);
            return false;
        } 
    }

    /**
         * Valida un archivo comprobando su existencia, tamaño máximo y extensión permitida.
         * @param archivo archivo a validar.
         * @param tamañoMaximo tamaño máximo en bytes.
         * @param extensionesPermitidas lista de extensiones permitidas (con punto), puede ser nula/vacía.
         * @return true si el archivo cumple las condiciones; false en caso contrario.
         */
        public static boolean validarArchivo(File archivo, long tamañoMaximo, List<String> extensionesPermitidas) {
        if ((archivo == null || !archivo.exists())||archivo.length() > tamañoMaximo){
            return false;
        }


        String nombre = archivo.getName().toLowerCase();

        return extensionesPermitidas == null || extensionesPermitidas.isEmpty() ||
                extensionesPermitidas.stream().anyMatch(nombre::endsWith);
    }

    /**
         * Genera un nombre de archivo único preservando la extensión original.
         * @param nombreOriginal nombre original del archivo (puede incluir extensión).
         * @return nombre único combinando base + UUID + extensión.
         */
        public static String generarNombreUnico(String nombreOriginal) {
        String ext = "";

        int idx = nombreOriginal.lastIndexOf('.');

        if (idx >= 0) {
            ext = nombreOriginal.substring(idx);
            nombreOriginal = nombreOriginal.substring(0, idx);
        }

        return nombreOriginal + "_" + UUID.randomUUID() + ext;
    }

    /**
     * Detecta el tipo MIME de un archivo leyendo su contenido (magic bytes).
     * Este método no depende de la extensión o la ruta del archivo.
     *
     * @param archivo El archivo del cual se quiere detectar el tipo MIME.
     * @return Un String con el tipo MIME (ej. "image/png", "application/pdf") o null si no se puede determinar.
     */
    public static String detectarMimeType(File archivo) {
        if (archivo == null || !archivo.exists()) {
            return null;
        }
        try (InputStream is = new FileInputStream(archivo)) {
            // Lee los primeros bytes del flujo para identificar el tipo de archivo.
            return URLConnection.guessContentTypeFromStream(is);
        } catch (IOException e) {
            log.warn("No se pudo detectar el tipo MIME de {}", archivo, e);
            return null;
        }
    }


    /**
         * Solicita al sistema operativo abrir el archivo con la aplicación predeterminada.
         * @param archivo archivo a abrir.
         * @return true si se lanzó correctamente; false si falla.
         */
        public static boolean abrirArchivo(File archivo) {
        try {
            if (archivo != null && archivo.exists() && Desktop.isDesktopSupported()) {
                // Pide al sistema operativo abrir el archivo con su aplicación predeterminada.
                Desktop.getDesktop().open(archivo);
                return true;
            }
        } catch (IOException e) {
            log.error("No se pudo abrir el archivo con la aplicación predeterminada: {}", archivo, e);
        }
        return false;
    }

    /**
         * Copia un archivo a un directorio de destino elegido por el usuario.
         * @param origen archivo a exportar (normalmente en 'media').
         * @param destinoDir carpeta de destino.
         * @return true si la exportación se completa; false si falla.
         */
        public static boolean exportarArchivo(File origen, File destinoDir) {
        if (origen == null || !origen.exists() || destinoDir == null){
            return false;
        }

        if (!destinoDir.exists()){
            destinoDir.mkdirs();
        }

        File destino = new File(destinoDir, origen.getName());

        try (InputStream in = new BufferedInputStream(new FileInputStream(origen));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {
            in.transferTo(out);
            return true;
        } catch (IOException e) {
            log.error("Error al exportar archivo {} a {}", origen, destinoDir, e);
            return false;
        }
    }

    /**
     * Construye una ruta como String dentro de la carpeta interna 'media'.
     * @param relative nombre de archivo o ruta relativa bajo 'media'.
     * @return String con la ruta completa relativa a la aplicación.
     */
    public static String getRutaMedia(String relative) {

        final String RUTAMEDIA = "media" + File.separator;

        if (relative == null || relative.isBlank()) {
            return RUTAMEDIA;
        }

        return RUTAMEDIA + relative;
    }

    /**
     * Crea un archivo ZIP a partir del texto de una conversación y una lista de mensajes que contienen los adjuntos.
     * Utiliza Streams para leer los archivos y escribirlos directamente en el ZIP sin crear archivos temporales.
     *
     * @param archivoZipDestino El archivo .zip que se va a crear.
     * @param conversacionTexto El contenido de la conversación como un String.
     * @param mensajes La lista de mensajes de la cual se extraerán los adjuntos.
     * @throws IOException Si ocurre un error de entrada/salida durante la creación del ZIP.
     */
    public static void crearArchivoZip(File archivoZipDestino, String conversacionTexto, List<Mensaje> mensajes) throws IOException {

        // 1. Usamos try-with-resources para asegurar que todos los streams se cierren solos.
        try (FileOutputStream fos = new FileOutputStream(archivoZipDestino);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // 2. Añadir el archivo de texto de la conversación al ZIP
            ZipEntry conversacionEntry = new ZipEntry(NOMBRE_TEXTO_CONVERSACION);
            zos.putNextEntry(conversacionEntry);
            zos.write(conversacionTexto.getBytes()); // Convertimos el String a bytes y lo escribimos
            zos.closeEntry();

            // 3. Añadir cada archivo adjunto leyendo su contenido con un InputStream
            for (Mensaje mensaje : mensajes) {
                if (mensaje.getAdjuntoRuta() != null && !mensaje.getAdjuntoRuta().isBlank()) {
                    File adjuntoFile = new File(getRutaMedia(mensaje.getAdjuntoRuta()));

                    if (adjuntoFile.exists()) {
                        // Creamos la entrada en el ZIP con el nombre original del archivo
                        ZipEntry adjuntoEntry = new ZipEntry(mensaje.getAdjuntoNombre());
                        zos.putNextEntry(adjuntoEntry);

                        // Leemos el adjunto con un FileInputStream y lo escribimos en el ZipOutputStream
                        try (FileInputStream fis = new FileInputStream(adjuntoFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
    }
}
