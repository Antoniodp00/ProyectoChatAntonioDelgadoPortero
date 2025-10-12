package com.dam.adp.proyectochatantoniodelgadoportero.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "mensajes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensajes {
    @XmlElement(name = "mensaje")
    private List<Mensaje> mensajeList = new ArrayList<>();

    public List<Mensaje> getMensajeList() {
        return mensajeList;
    }

    public void setMensajeList(List<Mensaje> mensajeList) {
        this.mensajeList = mensajeList;
    }

    public void addMensaje(Mensaje mensaje) {
        mensajeList.add(mensaje);
    }

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