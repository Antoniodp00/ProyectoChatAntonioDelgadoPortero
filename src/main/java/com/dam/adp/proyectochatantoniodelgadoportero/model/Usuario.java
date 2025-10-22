package com.dam.adp.proyectochatantoniodelgadoportero.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "usuario")
public class Usuario {
    private static final Logger log = LoggerFactory.getLogger(Usuario.class);
    private String nombreUsuario;
    private String contraseña;
    private String nombre;
    private String apellido;
    private String email;


    /**
     * Constructor por defecto necesario para frameworks de serialización/deserialización.
     */
    public Usuario() {}

    /**
     * Constructor completo del usuario.
     * @param nombreUsuario nombre único de usuario.
     * @param contraseña contraseña encriptada del usuario.
     * @param nombre nombre real.
     * @param apellido apellido del usuario.
     * @param email correo electrónico.
     */
    public Usuario(String nombreUsuario, String contraseña, String nombre, String apellido, String email) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    /**
     * Obtiene el nombre de usuario.
     * @return nombre de usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre de usuario.
     * @param nombreUsuario nuevo nombre de usuario.
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Obtiene la contraseña (hash) del usuario.
     * @return contraseña encriptada.
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * Establece la contraseña (hash) del usuario.
     * @param contraseña hash de la contraseña.
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * Obtiene el nombre real del usuario.
     * @return nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre real del usuario.
     * @param nombre nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     * @return apellido.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     * @param apellido nuevo apellido.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param email nuevo email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve una representación en cadena del usuario (su nombre de usuario).
     * @return El nombre de usuario.
     */
    @Override
    public String toString() {
        return this.getNombreUsuario();
    }
}