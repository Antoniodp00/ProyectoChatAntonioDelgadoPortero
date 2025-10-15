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

    /**
     * Abre la vista de inicio de sesión.
     * @param actionEvent evento del botón Iniciar sesión.
     */
    @FXML
    public void abrirLogin(ActionEvent actionEvent) {
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/inicioSesionView.fxml");
    }

    /**
     * Abre la vista de registro de usuario.
     * @param actionEvent evento del botón Registrarse.
     */
    @FXML
    public void abrirRegistro(ActionEvent actionEvent) {
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/registroView.fxml");
    }

}
