package com.dam.adp.proyectochatantoniodelgadoportero.app;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada de la aplicación JavaFX.
 */
public class Launcher {
    private static final Logger log = LoggerFactory.getLogger(Launcher.class);
    /**
     * Método principal que inicia la aplicación cargando la clase Aplicacion.
     * @param args argumentos de línea de comandos.
     *
     */
    public static void main(String[] args) {
        log.info("Lanzando JavaFX Application");
        Application.launch(Aplicacion.class, args);
    }
}
