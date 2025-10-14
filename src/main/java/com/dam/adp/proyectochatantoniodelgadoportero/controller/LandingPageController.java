package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
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
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/inicioSesionView.fxml");
    }

    @FXML
    public void abrirRegistro(ActionEvent actionEvent) {
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/registroView.fxml");
    }

}
