package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InicioSesionController {
    private static final Logger log = LoggerFactory.getLogger(InicioSesionController.class);

    public TextField txtNombreUsuario;
    public PasswordField txtContrasena;
    public Button btnLogin;
    public Label lblMensaje;

    /**
     * Intenta iniciar sesión con las credenciales introducidas y navega a la vista principal si son válidas.
     * @param actionEvent evento de acción del botón Iniciar sesión.
     */
    public void iniciarSesion(ActionEvent actionEvent) {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Todos los campos son obligatorios.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            log.warn("Intento de login con campos vacíos");
            return;
        }

        Usuario usuario = UsuarioDAO.validarCredenciales(nombreUsuario, contrasena);

        if (usuario != null) {
            lblMensaje.setText("");
            log.info("Login correcto para usuario {}", nombreUsuario);
            Sesion.getInstancia().iniciarSesion(usuario);

            Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/mainView.fxml");
        }else {
            lblMensaje.setText("Credenciales incorrectas.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            log.warn("Login fallido para usuario {}", nombreUsuario);
        }

    }

    /**
     * Vuelve a la pantalla de bienvenida (Landing Page).
     * @param actionEvent evento del botón Volver.
     */
    public void volverLanding(ActionEvent actionEvent) {
        log.info("Navegando a Landing Page desde Inicio de Sesión");
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }
}
