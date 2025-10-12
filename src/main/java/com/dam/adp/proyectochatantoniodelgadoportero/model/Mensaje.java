package com.dam.adp.proyectochatantoniodelgadoportero.model;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlRootElement(name = "mensaje")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensaje {
    private String remitente;
    private String destinatario;
    private String mensaje;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fecha;

    // Metadatos opcionales de adjunto
    private String adjuntoNombre; // nombre del archivo
    private String adjuntoRuta;   // ruta relativa en media/
    private long adjuntoTamano;   // tamaño en bytes
    private String adjuntoTipo;   // tipo MIME o extensión

    public Mensaje() {}

    public Mensaje(String remitente, String destinatario, String mensaje, LocalDateTime fecha) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getAdjuntoNombre() {
        return adjuntoNombre;
    }

    public void setAdjuntoNombre(String adjuntoNombre) {
        this.adjuntoNombre = adjuntoNombre;
    }

    public String getAdjuntoRuta() {
        return adjuntoRuta;
    }

    public void setAdjuntoRuta(String adjuntoRuta) {
        this.adjuntoRuta = adjuntoRuta;
    }

    public long getAdjuntoTamano() {
        return adjuntoTamano;
    }

    public void setAdjuntoTamano(long adjuntoTamano) {
        this.adjuntoTamano = adjuntoTamano;
    }

    public String getAdjuntoTipo() {
        return adjuntoTipo;
    }

    public void setAdjuntoTipo(String adjuntoTipo) {
        this.adjuntoTipo = adjuntoTipo;
    }

    @Override
    public String toString() {
        String base = "Mensaje {" +
                "remitente='" + remitente + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", contenido='" + mensaje + '\'' +
                ", fecha=" + (fecha != null ? fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "") +
                '}';
        if (adjuntoNombre != null) {
            base += " [Adjunto: " + adjuntoNombre + "]";
        }
        return base;
    }
}

