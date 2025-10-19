package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.ListaUsuarios;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    /**
     * Valida el formato de un correo electrónico sencillo mediante expresión regular.
     * @param email cadena a validar.
     * @return true si el email cumple el patrón, false en caso contrario.
     */
    public static boolean validarEmail(String email){
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            log.warn("El formato del correo no es válido: {}", email);
            return false;
        }
        return true;
    }

    /**
     * Método auxiliar para cambiar de escena en la ventana actual.
     *
     * @param fxmlPath Ruta del archivo FXML a cargar.
     */
    public static void cambiarEscena(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).getFirst();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            log.error("Error al cargar la vista: {}", fxmlPath, e);
        } catch (Exception e) {
            log.error("Error inesperado al cambiar de escena.", e);
        }
    }
}