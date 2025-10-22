package com.dam.adp.proyectochatantoniodelgadoportero.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "mensajes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensajes {
    private static final Logger log = LoggerFactory.getLogger(Mensajes.class);
    @XmlElement(name = "mensaje")
    private List<Mensaje> mensajeList = new ArrayList<>();

    /**
     * Devuelve la lista interna de mensajes.
     * @return lista de mensajes.
     */
    public List<Mensaje> getMensajeList() {
        return mensajeList;
    }

    /**
     * Establece la lista completa de mensajes.
     * @param mensajeList lista a asignar.
     */
    public void setMensajeList(List<Mensaje> mensajeList) {
        this.mensajeList = mensajeList;
        log.debug("Lista de mensajes establecida. Tamaño: {}", mensajeList != null ? mensajeList.size() : 0);
    }

    /**
     * Añade un mensaje a la lista interna.
     * @param mensaje objeto Mensaje a agregar.
     */
    public void addMensaje(Mensaje mensaje) {
        mensajeList.add(mensaje);
        log.debug("Mensaje añadido. Total ahora: {}", mensajeList.size());
    }

    /**
     * Devuelve una representación en cadena de la lista de mensajes.
     * @return Una cadena que representa la lista de mensajes.
     */
    @Override
    public String toString() {
        if (mensajeList.isEmpty()) {
            return "No hay mensajes guardados.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de mensajes:\n");
        sb.append("-----------------------------\n");
        for (Mensaje m : mensajeList) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}