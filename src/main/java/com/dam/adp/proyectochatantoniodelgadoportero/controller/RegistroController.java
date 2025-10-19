package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.PasswordManager;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistroController {
    private static final Logger log = LoggerFactory.getLogger(RegistroController.class);

    public TextField txtNombreUsuario;
    public PasswordField txtContrasena;
    public TextField txtNombre;
    public TextField txtApellido;
    public TextField txtEmail;
    public Button btnRegistrar;
    public Label lblMensaje;

    /**
     * Registra un nuevo usuario validando los campos y almacenando sus datos.
     * @param actionEvent evento del botón Registrar.
     */
    public void registrarUsuario(ActionEvent actionEvent) {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String contraseñaHasheada = PasswordManager.hashPassword(contrasena);
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            lblMensaje.setText("Todos los campos son obligatorios.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            log.warn("Registro fallido: campos obligatorios vacíos");
            return;
        }

        if (!Utils.validarEmail(email)) {
            lblMensaje.setText("El email no es válido.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            log.warn("Registro fallido: email no válido {}", email);
            return;
        }

        Usuario usuario = new Usuario(nombreUsuario, contraseñaHasheada, nombre, apellido, email);

        if (UsuarioDAO.añadirUsuario(usuario)) {
            lblMensaje.setText("Usuario registrado correctamente.");
            lblMensaje.setStyle("-fx-text-fill: green;");
            log.info("Usuario registrado: {} ({})", nombreUsuario, email);
            limpiarCampos();
        } else {
            lblMensaje.setText("Usuario con ese nombre de usuario (" + nombreUsuario + ") o email(" + email + ") ya existe");
            lblMensaje.setStyle("-fx-text-fill: red;");
            log.warn("Intento de registro duplicado: {} ({})", nombreUsuario, email);
        }
    }

    /**
     * Limpia los campos del formulario de registro.
     */
    private void limpiarCampos() {
        txtNombreUsuario.clear();
        txtContrasena.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
    }

    /**
     * Vuelve a la pantalla de inicio (Landing).
     * @param actionEvent evento del botón Volver.
     */
    public void volverLanding(ActionEvent actionEvent) {
        log.info("Navegando a Landing Page desde Registro");
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }
}
