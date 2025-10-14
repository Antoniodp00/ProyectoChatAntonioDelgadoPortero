package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileManager {

    private static final String RUTAMEDIA = "media" + File.separator;
    private static final String EXPORTACIONES = "exportaciones" + File.separator;
    private static final DateTimeFormatter EXPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FileManager() {}

    public static void asegurarDirectorios() {
        new File(RUTAMEDIA).mkdirs();
        new File(EXPORTACIONES).mkdirs();
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
                    if (estadistica == null) continue;
                    bw.write(estadistica);
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
                if (mensaje == null) continue;

                String fechaHora = (mensaje.getFecha() != null) ? mensaje.getFecha().format(EXPORT_FORMATTER) : "----";
                String contenido = mensaje.getMensaje() == null ? "" : mensaje.getMensaje().replace('\n', ' ');
                String remitente = mensaje.getRemitente() == null ? "" : mensaje.getRemitente();

                String linea = String.format("[%s] %s: %s", fechaHora, remitente, contenido);

                bw.write(linea);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
        final String SEPARADOR = ";";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDestino))) {
            // Escribir la cabecera del CSV
            bw.write("FechaHora" + SEPARADOR + "Remitente" + SEPARADOR + "Contenido");
            bw.newLine();

            List<Mensaje> lista = (mensajes != null) ? mensajes : java.util.Collections.emptyList();
            for (Mensaje mensaje : lista) {
                if (mensaje == null) continue;

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
            e.printStackTrace();
            return false;
        }
    }

    public static boolean guardarArchivo(File origen, String nombreArchivo) {
        asegurarDirectorios();
        File destino = new File(RUTAMEDIA + nombreArchivo);
        try (InputStream entrada = new BufferedInputStream(new FileInputStream(origen));
             OutputStream salida = new BufferedOutputStream(new FileOutputStream(destino))) {
            entrada.transferTo(salida);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } 
    }

    public static boolean validarArchivo(File archivo, long tamañoMaximo, List<String> extensionesPermitidas) {
        if (archivo == null || !archivo.exists()) return false;
        if (archivo.length() > tamañoMaximo) return false;

        String nombre = archivo.getName().toLowerCase();

        //Verifica la extensión: devuelve true si la lista de extensiones es nula/vacía (sin restricción)
        //    O si el nombre del archivo termina con alguna de las extensiones permitidas.
        return extensionesPermitidas == null || extensionesPermitidas.isEmpty() ||
                extensionesPermitidas.stream().anyMatch(nombre::endsWith);
    }

    public static String generarNombreUnico(String nombreOriginal) {
        String ext = "";

        int idx = nombreOriginal.lastIndexOf('.');

        if (idx >= 0) {
            //Extrae la extensión (incluyendo el punto).
            ext = nombreOriginal.substring(idx);
            //Extrae el nombre base.
            nombreOriginal = nombreOriginal.substring(0, idx);
        }

        //Combina el nombre original, un identificador universal único (UUID) y la extensión.
        //Esto previene que dos archivos con el mismo nombre se sobrescriban en la carpeta 'media'.
        return nombreOriginal + "_" + UUID.randomUUID() + ext;
    }

    public static String detectarMimeType(File archivo) {
        try {
            // Usa la API de Java NIO para intentar determinar el tipo de contenido/formato del archivo
            // basado en el sistema operativo (ej. "image/jpeg", "application/pdf").
            return Files.probeContentType(archivo.toPath());
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean abrirArchivo(File archivo) {
        try {
            // Verifica si el archivo existe y si la funcionalidad de escritorio está disponible.
            if (archivo != null && archivo.exists() && Desktop.isDesktopSupported()) {
                // Pide al sistema operativo abrir el archivo con su aplicación predeterminada.
                Desktop.getDesktop().open(archivo);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean exportarArchivo(File origen, File destinoDir) {
        if (origen == null || !origen.exists() || destinoDir == null) return false;

        //Crea el directorio de destino si no existe (el usuario lo selecciona).
        if (!destinoDir.exists()) destinoDir.mkdirs();

        File destino = new File(destinoDir, origen.getName());

        //Similar a guardarArchivo(), moviendo el archivo
        //desde la carpeta interna 'media' a la carpeta elegida por el usuario.
        try (InputStream in = new BufferedInputStream(new FileInputStream(origen));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {
            in.transferTo(out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Path getMediaPath(String relative) {
        //Si la ruta relativa es nula o vacía, devuelve solo la ruta base de 'media'.
        if (relative == null || relative.isBlank()) {
            return Paths.get(RUTAMEDIA);
        }
        //Combina la ruta base de 'media' con el nombre de archivo/ruta relativa proporcionada.
        return Paths.get(RUTAMEDIA + relative);
    }

    public static void crearArchivoZip(String rutaZip, List<File> archivos) {/* ... */ }
}