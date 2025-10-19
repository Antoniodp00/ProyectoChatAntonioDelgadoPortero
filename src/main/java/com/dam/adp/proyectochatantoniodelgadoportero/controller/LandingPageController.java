package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LandingPageController {

    private static final Logger log = LoggerFactory.getLogger(LandingPageController.class);

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
        log.info("Navegando a Inicio de Sesión desde Landing Page");
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/inicioSesionView.fxml");
    }

    /**
     * Abre la vista de registro de usuario.
     * @param actionEvent evento del botón Registrarse.
     */
    @FXML
    public void abrirRegistro(ActionEvent actionEvent) {
        log.info("Navegando a Registro desde Landing Page");
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/registroView.fxml");
    }

}
