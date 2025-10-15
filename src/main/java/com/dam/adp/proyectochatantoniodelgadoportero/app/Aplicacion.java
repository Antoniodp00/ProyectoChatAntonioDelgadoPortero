package com.dam.adp.proyectochatantoniodelgadoportero.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX que carga la vista inicial.
 */
public class Aplicacion extends Application {
    /**
     * Inicializa y muestra la ventana principal.
     * @param stage escenario principal proporcionado por JavaFX.
     * @throws IOException si ocurre un error al cargar el FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Aplicacion.class.getResource("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Pagina Principal");
        stage.setScene(scene);
        stage.show();
    }
}