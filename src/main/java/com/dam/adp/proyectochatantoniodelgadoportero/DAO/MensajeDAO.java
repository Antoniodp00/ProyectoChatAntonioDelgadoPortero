package com.dam.adp.proyectochatantoniodelgadoportero.DAO;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.FileManager;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.XMLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MensajeDAO {

    private static final Logger log = LoggerFactory.getLogger(MensajeDAO.class);
    private static final Path RUTA_XML = Paths.get("data", "mensajes.xml");

    /**
     * Envía un mensaje agregándolo al XML.
     *
     * @param remitente   nombre del usuario que envía el mensaje
     * @param destinatario nombre del usuario que recibe el mensaje
     * @param contenido    texto del mensaje
     */
    public static void enviarMensaje(String remitente, String destinatario, String contenido) {
        try {
            Mensajes mensajes = cargarMensajes();

            Mensaje nuevoMensaje = new Mensaje(remitente, destinatario, contenido, LocalDateTime.now());
            mensajes.getMensajeList().add(nuevoMensaje);

            guardarMensajes(mensajes);

            log.info("Mensaje enviado correctamente de {} a {}", remitente, destinatario);
        } catch (Exception e) {
            log.error("Error al enviar mensaje: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el mensaje", e);
        }
    }


    /**
     * Envía un mensaje con un archivo adjunto. Si la validación o guardado del archivo falla,
     * el mensaje se envía igualmente SIN adjunto (se mantiene el mensaje).
     */
    public static void enviarMensajeConAdjunto(String remitente, String destinatario, String contenido, File archivo) {
        try {
            Mensajes mensajes = cargarMensajes();
            Mensaje nuevo = new Mensaje(remitente, destinatario, contenido, LocalDateTime.now());

            if (archivo != null) {
                long max = 10L * 1024 * 1024; // 10 MB
                List<String> permitidas = Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".pdf", ".txt", ".docx", ".xlsx");
                boolean validado = FileManager.validarArchivo(archivo, max, permitidas);
                if (validado) {
                    String nombreDestino = FileManager.generarNombreUnico(archivo.getName());
                    if (FileManager.guardarArchivo(archivo, nombreDestino)) {
                        nuevo.setAdjuntoNombre(archivo.getName());
                        nuevo.setAdjuntoRuta(nombreDestino); // ruta relativa bajo media/
                        nuevo.setAdjuntoTamano(archivo.length());
                        String mime = FileManager.detectarMimeType(archivo);
                        nuevo.setAdjuntoTipo(mime != null ? mime : obtenerExtension(archivo.getName()));
                    }
                }
            }
            mensajes.getMensajeList().add(nuevo);
            guardarMensajes(mensajes);
        } catch (Exception e) {
            log.error("Error al enviar mensaje con adjunto: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el mensaje", e);
        }
    }

    private static String obtenerExtension(String nombre) {
        int i = nombre.lastIndexOf('.');
        return i >= 0 ? nombre.substring(i + 1) : "";
    }


    /**
     * Carga los mensajes desde el XML.
     *
     * @return objeto Mensajes, vacío si el XML no existe o está vacío
     */
    private static Mensajes cargarMensajes() {
        Mensajes mensajesVacios = new Mensajes();
        Mensajes leidos = XMLManager.readXML(mensajesVacios, RUTA_XML.toString());
        return leidos != null ? leidos : mensajesVacios;
    }

    /**
     * Guarda los mensajes en el XML. Método sincronizado para evitar problemas de concurrencia.
     *
     * @param mensajes objeto Mensajes a guardar
     */
    private static synchronized void guardarMensajes(Mensajes mensajes) {
        try {
            XMLManager.writeXML(mensajes, RUTA_XML.toString());
        } catch (Exception e) {
            log.error("No se pudo guardar el XML de mensajes: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo guardar el XML de mensajes", e);
        }
    }


    /**
     * Devuelve todos los mensajes entre dos usuarios.
     *
     * @param usuario1 primer usuario
     * @param usuario2 segundo usuario
     * @return lista de mensajes filtrados
     */
    public static Mensajes listarMensajesEntre(String usuario1, String usuario2) {
        Mensajes mensajesFiltrados = new Mensajes();
        Mensajes mensajes = cargarMensajes();

        if (mensajes.getMensajeList().isEmpty()) {
            return mensajesFiltrados;
        }

            String u1 = usuario1 == null ? "" : usuario1.trim().toLowerCase();
            String u2 = usuario2 == null ? "" : usuario2.trim().toLowerCase();

        for (Mensaje mensaje : mensajes.getMensajeList()) {
            if (mensaje == null) continue;
            String r = mensaje.getRemitente() == null ? "" : mensaje.getRemitente().trim().toLowerCase();
            String d = mensaje.getDestinatario() == null ? "" : mensaje.getDestinatario().trim().toLowerCase();

            boolean esEntreUsuarios = (r.equals(u1) && d.equals(u2)) || (r.equals(u2) && d.equals(u1));

            if (esEntreUsuarios) {
                mensajesFiltrados.addMensaje(mensaje);
            }
        }
        return mensajesFiltrados;
    }
}