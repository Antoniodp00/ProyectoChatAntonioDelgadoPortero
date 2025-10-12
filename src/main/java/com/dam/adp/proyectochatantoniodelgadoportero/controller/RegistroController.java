package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {
    public TextField txtNombreUsuario;
    public PasswordField txtContrasena;
    public TextField txtNombre;
    public TextField txtApellido;
    public TextField txtEmail;
    public Button btnRegistrar;
    public Label lblMensaje;

    public void registrarUsuario(ActionEvent actionEvent) {
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            lblMensaje.setText("Todos los campos son obligatorios.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!Utilidades.validarEmail(email)) {
            lblMensaje.setText("El email no es válido.");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Usuario usuario = new Usuario(nombreUsuario, contrasena, nombre, apellido, email);

        if (UsuarioDAO.añadirUsuario(usuario)) {
            lblMensaje.setText("Usuario registrado correctamente.");
            lblMensaje.setStyle("-fx-text-fill: green;");
            limpiarCampos();
        } else {
            lblMensaje.setText("Usuario con ese nombre de usuario (" + nombreUsuario + ") o email(" + email + ") ya existe");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    private void limpiarCampos() {
        txtNombreUsuario.clear();
        txtContrasena.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
    }

    public void volverLanding(ActionEvent actionEvent) {
        Utilidades.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }
}
