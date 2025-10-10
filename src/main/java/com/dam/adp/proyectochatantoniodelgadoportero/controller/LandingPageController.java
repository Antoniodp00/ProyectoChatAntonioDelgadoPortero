package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utilidades;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
        Utilidades.cambiarEscena("/org/dam2/adp/proyectochatadp/registroView.fxml");
    }

}
