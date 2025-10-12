package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LandingPageController {


    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistrarse;

    @FXML
    public void abrirLogin(ActionEvent actionEvent) {
        Utilidades.cambiarEscena("/org/dam2/adp/proyectochatadp/inicioSesionView.fxml");
    }

    @FXML
    public void abrirRegistro(ActionEvent actionEvent) {
        Utilidades.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/registroView.fxml");
    }

}
