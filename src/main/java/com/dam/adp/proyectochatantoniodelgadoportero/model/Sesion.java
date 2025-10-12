package com.dam.adp.proyectochatantoniodelgadoportero.model;

public class Sesion {

    private static Sesion instancia;
    private Usuario usuarioLogueado;

    // Constructor privado: evita que se creen instancias desde fuera
    private Sesion() {}

    // Método para obtener la instancia única
    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    // Guardar el usuario logueado
    public void iniciarSesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    // Cerrar sesión
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    // Obtener el usuario actual
    public Usuario getUsuario() {
        return usuarioLogueado;
    }

    // Saber si hay alguien logueado
    public boolean haySesionActiva() {
        return usuarioLogueado != null;
    }
}
