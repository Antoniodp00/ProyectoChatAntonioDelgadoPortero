package com.dam.adp.proyectochatantoniodelgadoportero.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sesion {

    private static final Logger log = LoggerFactory.getLogger(Sesion.class);
    private static Sesion instancia;
    private Usuario usuarioLogueado;

    // Constructor privado: evita que se creen instancias desde fuera
    private Sesion() {}

    /**
     * Obtiene la instancia única (patrón Singleton) de la sesión.
     * @return instancia global de Sesion.
     */
    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
            log.debug("Instancia de Sesion creada");
        }
        return instancia;
    }

    /**
     * Establece el usuario actual como logueado en la sesión.
     * @param usuario usuario autenticado.
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
        log.info("Sesión iniciada para usuario: {}", usuario != null ? usuario.getNombreUsuario() : "null");
    }

    /**
     * Cierra la sesión actual (borra el usuario logueado).
     */
    public void cerrarSesion() {
        log.info("Sesión cerrada para usuario: {}", usuarioLogueado != null ? usuarioLogueado.getNombreUsuario() : "null");
        this.usuarioLogueado = null;
    }

    /**
     * Devuelve el usuario actualmente logueado.
     * @return usuario de la sesión o null si no hay.
     */
    public Usuario getUsuario() {
        return usuarioLogueado;
    }

    /**
     * Indica si existe un usuario con sesión activa.
     * @return true si hay usuario logueado; false en caso contrario.
     */
    public boolean haySesionActiva() {
        return usuarioLogueado != null;
    }
}
