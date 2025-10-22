package com.dam.adp.proyectochatantoniodelgadoportero.model;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "mensaje")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensaje {
    private static final Logger log = LoggerFactory.getLogger(Mensaje.class);
    private String remitente;
    private String destinatario;
    private String mensaje;
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime fecha;

    // Datos del adjunto
    private String adjuntoNombre; // nombre del archivo
    private String adjuntoRuta;   // ruta relativa en media/
    private long adjuntoTamano;   // tamaño en bytes
    private String adjuntoTipo;   // tipo extensión

    /**
     * Constructor por defecto.
     */
    public Mensaje() {}

    /**
     * Constructor para crear un mensaje con los datos básicos.
     * @param remitente El remitente del mensaje.
     * @param destinatario El destinatario del mensaje.
     * @param mensaje El contenido del mensaje.
     * @param fecha La fecha y hora del mensaje.
     */
    public Mensaje(String remitente, String destinatario, String mensaje, LocalDateTime fecha) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    /**
     * Obtiene el remitente del mensaje.
     * @return El remitente del mensaje.
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Establece el remitente del mensaje.
     * @param remitente El remitente del mensaje.
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * Obtiene el destinatario del mensaje.
     * @return El destinatario del mensaje.
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Establece el destinatario del mensaje.
     * @param destinatario El destinatario del mensaje.
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    /**
     * Obtiene el contenido del mensaje.
     * @return El contenido del mensaje.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el contenido del mensaje.
     * @param mensaje El contenido del mensaje.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene la fecha y hora del mensaje.
     * @return La fecha y hora del mensaje.
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora del mensaje.
     * @param fecha La fecha y hora del mensaje.
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el nombre del archivo adjunto.
     * @return El nombre del archivo adjunto.
     */
    public String getAdjuntoNombre() {
        return adjuntoNombre;
    }

    /**
     * Establece el nombre del archivo adjunto.
     * @param adjuntoNombre El nombre del archivo adjunto.
     */
    public void setAdjuntoNombre(String adjuntoNombre) {
        this.adjuntoNombre = adjuntoNombre;
    }

    /**
     * Obtiene la ruta del archivo adjunto.
     * @return La ruta del archivo adjunto.
     */
    public String getAdjuntoRuta() {
        return adjuntoRuta;
    }

    /**
     * Establece la ruta del archivo adjunto.
     * @param adjuntoRuta La ruta del archivo adjunto.
     */
    public void setAdjuntoRuta(String adjuntoRuta) {
        this.adjuntoRuta = adjuntoRuta;
    }

    /**
     * Obtiene el tamaño del archivo adjunto.
     * @return El tamaño del archivo adjunto.
     */
    public long getAdjuntoTamano() {
        return adjuntoTamano;
    }

    /**
     * Establece el tamaño del archivo adjunto.
     * @param adjuntoTamano El tamaño del archivo adjunto.
     */
    public void setAdjuntoTamano(long adjuntoTamano) {
        this.adjuntoTamano = adjuntoTamano;
    }

    /**
     * Obtiene el tipo (extensión) del archivo adjunto.
     * @return El tipo del archivo adjunto.
     */
    public String getAdjuntoTipo() {
        return adjuntoTipo;
    }

    /**
     * Establece el tipo (extensión) del archivo adjunto.
     * @param adjuntoTipo El tipo del archivo adjunto.
     */
    public void setAdjuntoTipo(String adjuntoTipo) {
        this.adjuntoTipo = adjuntoTipo;
    }

    /**
     * Devuelve una representación en cadena del mensaje.
     * @return Una cadena que representa el objeto Mensaje.
     */
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
