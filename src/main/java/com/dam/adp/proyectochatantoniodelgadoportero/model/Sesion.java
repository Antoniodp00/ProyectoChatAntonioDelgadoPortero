package com.dam.adp.proyectochatantoniodelgadoportero.model;

public class Sesion {

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
        }
        return instancia;
    }

    /**
     * Establece el usuario actual como logueado en la sesión.
     * @param usuario usuario autenticado.
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /**
     * Cierra la sesión actual (borra el usuario logueado).
     */
    public void cerrarSesion() {
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
