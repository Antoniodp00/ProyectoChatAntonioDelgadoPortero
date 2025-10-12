package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InicioSesionController {
    public TextField txtNombreUsuario;
    public PasswordField txtContrasena;
    public Button btnLogin;
    public Label lblMensaje;

    public void iniciarSesion(ActionEvent actionEvent) {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Todos los campos son obligatorios.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Usuario usuario = UsuarioDAO.validarCredenciales(nombreUsuario, contrasena);

        if (usuario != null) {
            lblMensaje.setText("");

            Sesion.getInstancia().iniciarSesion(usuario);

            Utilidades.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/mainView.fxml");
        }else {
            lblMensaje.setText("Credenciales incorrectas.");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }

    }

    public void volverLanding(ActionEvent actionEvent) {
        Utilidades.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }
}
