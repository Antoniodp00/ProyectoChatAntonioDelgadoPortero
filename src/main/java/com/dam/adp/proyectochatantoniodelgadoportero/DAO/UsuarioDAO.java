package com.dam.adp.proyectochatantoniodelgadoportero.DAO;


import com.dam.adp.proyectochatantoniodelgadoportero.model.ListaUsuarios;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.PasswordManager;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.XMLManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UsuarioDAO {

    private static final Path RUTA_XML = Paths.get("data", "usuarios.xml");

    /**
     * Leer usuarios desde el XML. Si no existe, devuelve una lista vacía.
     */
    public static ListaUsuarios leerUsuarios() {
        ListaUsuarios listaUsuarios = new ListaUsuarios();
        ListaUsuarios leidos = XMLManager.readXML(listaUsuarios, RUTA_XML.toString());
        return leidos != null ? leidos : listaUsuarios;
    }

    /**
     * Guarda la lista de usuarios en el XML
     */
    public static void guardarUsuarios(ListaUsuarios listaUsuarios) {
        try {
            XMLManager.writeXML(listaUsuarios, RUTA_XML.toString());
        } catch (Exception e) {
            System.err.println("Error al guardar usuarios en XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Añade un usuario si no existe en la lista
     * @return true si se añade, false si ya existe
     */
    public static boolean añadirUsuario(Usuario usuarioNuevo) {
        ListaUsuarios listaUsuarios = leerUsuarios();

        for (Usuario usuario : listaUsuarios.getLista()) {
            if (usuario.getEmail().equalsIgnoreCase(usuarioNuevo.getEmail()) ||
                    usuario.getNombreUsuario().equalsIgnoreCase(usuarioNuevo.getNombreUsuario())) {
                System.out.println("El usuario ya existe en el sistema");
                return false;
            }
        }

        listaUsuarios.getLista().add(usuarioNuevo);
        guardarUsuarios(listaUsuarios);
        System.out.println("Usuario registrado correctamente");
        return true;
    }

    /**
     * Listar todos los usuarios
     */
    public static void listarUsuarios() {
        ListaUsuarios listaUsuarios = leerUsuarios();
        if (listaUsuarios.getLista().isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario usuario : listaUsuarios.getLista()) {
            System.out.println(usuario);
        }
    }

    /**
     * Devuelve un usuario por nombre de usuario, o null si no existe
     */
    public static Usuario cargarUsuario(String nombreUsuario) {
        ListaUsuarios listaUsuarios = leerUsuarios();
        for (Usuario u : listaUsuarios.getLista()) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null; // no encontrado
    }

    /**
     * Valida las credenciales de un usuario
     * @param nombreUsuario
     * @param password
     * @return Usuario si las credenciales son correctas, null si no
     */
    public static Usuario validarCredenciales(String nombreUsuario, String password) {
        Usuario usuario = cargarUsuario(nombreUsuario);
        if (usuario != null && PasswordManager.checkPassword(password, usuario.getContraseña())) {
            return usuario;
        }
        return null;
    }

}
