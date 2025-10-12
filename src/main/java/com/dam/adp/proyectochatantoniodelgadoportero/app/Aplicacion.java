package com.dam.adp.proyectochatantoniodelgadoportero.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {
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