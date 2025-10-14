package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.MensajeDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.FileManager;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.StreamUtils;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    private Stage stage;


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

    private Stage getStage() {

        if (stage == null && lblEstado != null) {
            stage = (Stage) lblEstado.getScene().getWindow();
        }
        return stage;
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
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
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
        lblMensajesPorUsuario.setText(StreamUtils.formatearConteoUsuario(porUsuario));

        //Palabras mas comunes
        Map<String, Long> topPalabras = StreamUtils.palabraMasComun(mensajes.getMensajeList(), 5);
        lblPalabrasComunes.setText(StreamUtils.formatearTopPalabras(topPalabras));
    }

    @FXML
    public void exportarTxt(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) {
            lblEstado.setText("Error: No hay usuario seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        //Obtener todos los mensajes
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
        if (mensajes.getMensajeList().isEmpty()) {
            lblEstado.setText("No hay mensajes para exportar");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar conversacion a TXT");

        //Nombre del archivo
        String nombreArchivo = usuarioLogueado.getNombreUsuario() + "-" + usuarioSeleccionado.getNombreUsuario() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        fileChooser.setInitialFileName(nombreArchivo + ".txt");

        //Filtrar extension
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(getStage());

        if (file != null) {
            boolean exito = FileManager.exportarAArchivoTexto(mensajes.getMensajeList(), file);
            if (exito) {
                lblEstado.setText("Exportado a: " + file.getAbsolutePath() + " con exito.");
                lblEstado.setStyle("-fx-text-fill: green;");
            } else {
                lblEstado.setText("Error al exportar");
                lblEstado.setStyle("-fx-text-fill: red;");
            }
        } else {
            lblEstado.setText("Operacion cancelada");
            lblEstado.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void exportarCsv(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) {
            lblEstado.setText("Error: No hay usuario seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
        if (mensajes.getMensajeList().isEmpty()) {
            lblEstado.setText("No hay mensajes para exportar");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar conversacion a CSV");

        String nombreArchivo = usuarioLogueado.getNombreUsuario() + "-" + usuarioSeleccionado.getNombreUsuario() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        fileChooser.setInitialFileName(nombreArchivo + ".csv");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de Texto (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(getStage());

        if (file != null) {
            boolean exito = FileManager.exportarAArchivoCsv(mensajes.getMensajeList(), file);

            if (exito) {
                lblEstado.setText("Exportado a: " + file.getAbsolutePath() + " con exito.");
                lblEstado.setStyle("-fx-text-fill: green;");
            } else {
                lblEstado.setText("Error al exportar");
                lblEstado.setStyle("-fx-text-fill: red;");
            }
        } else {
            lblEstado.setText("Operacion cancelada");
        }
    }

    @FXML
    public void generarResumen(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) {
            lblEstado.setText("Error: No hay usuario seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        ArrayList<String> estadisticasGeneradas = generaEstadisticasTexto();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Estadísticas (TXT)");

        String nombreSugerido = "Stats_" + usuarioLogueado.getNombreUsuario() + usuarioSeleccionado.getNombreUsuario() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        fileChooser.setInitialFileName(nombreSugerido + ".txt");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(getStage());

        if (file != null) {
            boolean exito = FileManager.exportarEstadisticas(estadisticasGeneradas, file);

            if (exito) {
                lblEstado.setText("Estadísticas exportadas con éxito.");
                lblEstado.setStyle("-fx-text-fill: green;");
            } else {
                lblEstado.setText("Error al exportar estadísticas.");
                lblEstado.setStyle("-fx-text-fill: red;");
            }
        } else {
            lblEstado.setText("Exportación de estadísticas cancelada.");
            lblEstado.setStyle("-fx-text-fill: gray;");
        }
    }

    /**
     * Genera y formatea todas las estadísticas de la conversación en una lista de Strings.
     * Nota: Debe ser un método de la clase (no privado si se necesita acceder desde otro lado,
     * pero 'private' es suficiente aquí).
     * * @return ArrayList<String> con cada línea de estadística, o una lista vacía si no hay mensajes.
     */
    private ArrayList<String> generaEstadisticasTexto(){

        // 1. Obtener la conversación
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
        List<Mensaje> listaMensajes = mensajes.getMensajeList();

        if (listaMensajes.isEmpty()) {
            lblEstado.setText("No hay mensajes para generar estadísticas.");
            lblEstado.setStyle("-fx-text-fill: orange;");
            return new ArrayList<>();
        }

        ArrayList<String> estadisticasGeneradas = new ArrayList<>();


        estadisticasGeneradas.add("--- Resumen de Conversación ---");
        estadisticasGeneradas.add("Usuarios: " + usuarioLogueado.getNombreUsuario() + " y " + usuarioSeleccionado.getNombreUsuario());
        estadisticasGeneradas.add("Fecha de Generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        estadisticasGeneradas.add("-------------------------------");

        int totalMensajes = StreamUtils.contarMensajes(listaMensajes);
        estadisticasGeneradas.add("Total de Mensajes: " + totalMensajes);

        Map<String, Long> porUsuario = StreamUtils.contarMensajesPorUsuario(listaMensajes);
        estadisticasGeneradas.add("Mensajes por Usuario: " + StreamUtils.formatearConteoUsuario(porUsuario));

        Map<String, Long> topPalabras = StreamUtils.palabraMasComun(listaMensajes, 5);
        estadisticasGeneradas.add("Top 5 Palabras Comunes: " + StreamUtils.formatearTopPalabras(topPalabras));

        estadisticasGeneradas.add("-------------------------------");

        return estadisticasGeneradas;
    }

    }
