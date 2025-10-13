package com.dam.adp.proyectochatantoniodelgadoportero.DAO;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.XMLManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MensajeDAO {

    // Ruta relativa al proyecto
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

            System.out.println("Mensaje enviado correctamente de " + remitente + " a " + destinatario);
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo enviar el mensaje", e);
        }
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
            System.err.println("Error al guardar mensajes en XML: " + e.getMessage());
            e.printStackTrace();
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

        if (mensajes.getMensajeList().isEmpty()){
            return mensajesFiltrados;
        }

        for (Mensaje mensaje : mensajes.getMensajeList()) {
            boolean esEntreUsuarios = (mensaje.getRemitente().trim().equalsIgnoreCase(usuario1.trim()) &&
                    mensaje.getDestinatario().trim().equalsIgnoreCase(usuario2.trim())) ||
                    (mensaje.getRemitente().trim().equalsIgnoreCase(usuario2.trim()) &&
                            mensaje.getDestinatario().trim().equalsIgnoreCase(usuario1.trim()));

            if (esEntreUsuarios){
                mensajesFiltrados.getMensajeList().add(mensaje);
            }
        }
        return mensajesFiltrados;
    }
}