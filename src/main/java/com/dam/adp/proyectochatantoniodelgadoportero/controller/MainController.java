package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.MensajeDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.StreamUtils;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utilidades;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class MainController {

    public VBox menuLateral;
    public ListView listaUsuarios;
    public Button btnCerrarSesion;
    public VBox panelChat;
    public Label lblUsuarioChat;
    public Button btnGenerarResumen;
    public Label lblTotalMensajes;
    public Label lblMensajesPorUsuario;
    public Label lblPalabrasComunes;
    public TextArea txtChat;
    public TextField txtMensaje;
    public Button btnAdjuntar;
    public Button btnEnviar;
    public ListView listaAdjuntos;
    public Button btnQuitarAdjunto;
    public Button btnAbrirAdjunto;
    public Button btnExportarAdjunto;
    public CheckBox chkSoloAdjuntos;
    public Button btnExportarTxt;
    public Button btnExportarCsv;
    public Button btnExportarZip;
    public Label lblEstado;

    private Usuario usuarioLogueado;
    private Usuario usuarioSeleccionado;


    @FXML
    public void initialize() {

        usuarioLogueado = Sesion.getInstancia().getUsuario();

        ObservableList<Usuario> usuariosObservableList = FXCollections.observableArrayList(UsuarioDAO.leerUsuarios().getLista());

        usuariosObservableList.removeIf(usuario -> usuario.getNombre().equals(usuarioLogueado.getNombre()));

        listaUsuarios.setItems(usuariosObservableList);


        //Seleccionar un usuario de la lista
        listaUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                usuarioSeleccionado = (Usuario) newValue;
                lblUsuarioChat.setText(usuarioSeleccionado.getNombreUsuario());
                mostrarMensajes();
                cargarEstadisticas();
            }
        });
    }

    public void mostrarMensajes() {
        if (usuarioSeleccionado == null) return;

        txtChat.clear();
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());

        for (Mensaje mensaje : mensajes.getMensajeList()) {

            String nombreRemitente = mensaje.getRemitente();
            String remitenteFormateado = nombreRemitente;

            // Compara si el remitente del mensaje es el usuario actual logueado
            if (nombreRemitente.equals(usuarioLogueado.getNombreUsuario())) {
                remitenteFormateado = "Tú"; // Cambia el nombre a "Tú"
            }

            String contenido = mensaje.getMensaje();
            String fecha = (mensaje.getFecha() != null)
                    ? mensaje.getFecha().format(DateTimeFormatter.ofPattern("HH:mm"))
                    : "--:--";

            // Uso de la variable formateada
            String lineaChat = String.format("[%s] %s: %s\n", fecha, remitenteFormateado, contenido);

            txtChat.appendText(lineaChat);
        }
        // Scroll automatico
        txtChat.selectPositionCaret(txtChat.getLength());
        txtChat.deselect();
    }


    public void cerrarSesion(ActionEvent actionEvent) {
        Sesion.getInstancia().cerrarSesion();
        Utilidades.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }

    @FXML
    public void enviarMensaje(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) return;

        String mensaje = txtMensaje.getText().trim();

        MensajeDAO.enviarMensaje(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario(), mensaje);

        mostrarMensajes();
        cargarEstadisticas();
        txtMensaje.clear();

    }

    public void generarResumen(ActionEvent actionEvent) {
    }

    private void cargarEstadisticas() {
        if (usuarioSeleccionado == null) {
            lblTotalMensajes.setText("-");
            lblMensajesPorUsuario.setText("-");
            lblPalabrasComunes.setText("-");
            return;
        }

        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());

        //Total de mensajes
        int totalMensajes = StreamUtils.contarMensajes(mensajes.getMensajeList());
        lblTotalMensajes.setText(String.valueOf(totalMensajes));


        //Mensajes por usuario
        Map<String, Long> porUsuario = StreamUtils.contarMensajesPorUsuario(mensajes.getMensajeList());
        lblMensajesPorUsuario.setText(  StreamUtils.formatearConteoUsuario(porUsuario));

        //Palabras mas comunes
        Map<String, Long> topPalabras = StreamUtils.palabraMasComun(mensajes.getMensajeList(), 5);
        lblPalabrasComunes.setText(StreamUtils.formatearTopPalabras(topPalabras));
    }
}
