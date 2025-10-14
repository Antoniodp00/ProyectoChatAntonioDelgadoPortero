package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManager {

    private static final String RUTAMEDIA = "media" + File.separator;
    private static final String EXPORTACIONES = "exportaciones" + File.separator;
    private static final DateTimeFormatter EXPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private FileManager() {}
    public static void ensureDirs() {
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
            for (String estadistica : estadisticas) {
                bw.write(estadistica);
                bw.newLine();
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
            for (Mensaje mensaje : mensajes) {
                if (mensaje == null) continue;

                String fechaHora = (mensaje.getFecha() != null)
                        ? mensaje.getFecha().format(EXPORT_FORMATTER)
                        : "----";

                String linea = String.format("[%s] %s: %s",
                        fechaHora,
                        mensaje.getRemitente(),
                        mensaje.getMensaje().replace('\n', ' '));

                bw.write(linea);
                bw.newLine();
            }
            return true; // Éxito
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Fallo
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

            for (Mensaje mensaje : mensajes) {
                if (mensaje == null) continue;

                String fechaHora = (mensaje.getFecha() != null)
                        ? mensaje.getFecha().format(EXPORT_FORMATTER)
                        : "----";

                String contenidoLimpio = mensaje.getMensaje()
                        .replace(SEPARADOR, "")
                        .replace('\n', ' ');

                String linea = fechaHora + SEPARADOR +
                        mensaje.getRemitente() + SEPARADOR +
                        contenidoLimpio;

                bw.write(linea);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean guardarArchivo(File origen, String nombreArchivo) { /* ... */ return true; }
    public static boolean validarArchivo(File archivo, long tamañoMaximo, List<String> extensionesPermitidas) { /* ... */ return true; }
    public static String generarNombreUnico(String nombreOriginal) { /* ... */ return ""; }
    public static String detectarMimeType(File archivo) { /* ... */ return null; }
    public static void crearArchivoZip(String rutaZip, List<File> archivos) { /* ... */ }
    public static boolean abrirArchivo(File archivo) { /* ... */ return true; }
    public static boolean exportarArchivo(File origen, File destinoDir) { /* ... */ return true; }
    public static Path getMediaPath(String relative) { /* ... */ return Paths.get(""); }
}