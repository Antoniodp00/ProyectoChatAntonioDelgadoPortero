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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainController {

    public ListView<String> listaAdjuntos;
    public Button btnQuitarAdjunto;
    public Button btnAbrirAdjunto;
    public Button btnExportarAdjunto;
    public Label lblEstado;
    public Button btnExportarZip;
    public Button btnExportarCsv;
    public Button btnExportarTxt;
    public CheckBox chkSoloAdjuntos;
    public Button btnAdjuntar;
    public Label lblPalabrasComunes;
    public Label lblMensajesPorUsuario;
    public Label lblTotalMensajes;
    public Button btnGenerarResumen;
    public Button btnCerrarSesion;
    public VBox menuLateral;
    @FXML
    private ListView<Usuario> listaUsuarios;
    @FXML
    private Label lblUsuarioChat;
    @FXML
    private TextArea txtChat;
    @FXML
    private TextField txtMensaje;
    @FXML
    private Button btnEnviar;

    private Usuario usuarioLogueado;
    private Usuario usuarioSeleccionado;
    private File adjuntoSeleccionado;

    private Stage stage;


    /**
     * Inicializa la vista principal: carga usuarios, configura listeners y botones.
     */
    @FXML
    public void initialize() {

        usuarioLogueado = Sesion.getInstancia().getUsuario();

        ObservableList<Usuario> usuariosObservableList = FXCollections.observableArrayList(UsuarioDAO.leerUsuarios().getLista());

        usuariosObservableList.removeIf(usuario -> usuario.getNombre().equals(usuarioLogueado.getNombre()));

        listaUsuarios.setItems(usuariosObservableList);

        //Seleccionar un usuario de la lista
        listaUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                usuarioSeleccionado = newValue;
                lblUsuarioChat.setText("Chat con: " + usuarioSeleccionado.getNombreUsuario());
                mostrarMensajes();
                cargarEstadisticas();
            }
        });

        btnAdjuntar.setOnAction(e -> seleccionarAdjunto());
        btnQuitarAdjunto.setOnAction(e -> quitarAdjunto());
        btnAbrirAdjunto.setOnAction(e -> abrirAdjunto());
        btnExportarAdjunto.setOnAction(e -> exportarAdjunto());
        chkSoloAdjuntos.setOnAction(e -> mostrarMensajes());
    }

    /**
     * Obtiene y memoriza el Stage actual asociado a la vista.
     * Se usa para abrir diálogos (FileChooser/DirectoryChooser) desde este controlador.
     * @return Stage de la ventana actual, o null si aún no está disponible.
     */
    private Stage getStage() {

        if (stage == null && lblEstado != null) {
            stage = (Stage) lblEstado.getScene().getWindow();
        }
        return stage;
    }

    /**
     * Muestra los mensajes de la conversación con el usuario seleccionado.
     * Si está activado "Solo adjuntos", filtra para mostrar únicamente mensajes que contengan adjuntos.
     * También rellena la lista lateral de adjuntos detectados en la conversación.
     */
    private void mostrarMensajes() {
         if (usuarioSeleccionado == null) return;

        txtChat.clear();
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());

        boolean soloAdjuntos = chkSoloAdjuntos != null && chkSoloAdjuntos.isSelected();
        List<String> adjuntosConversacion = new ArrayList<>();
        for (Mensaje mensaje : mensajes.getMensajeList()){
            boolean tieneAdjunto = 
                    (mensaje.getAdjuntoRuta() != null && !mensaje.getAdjuntoRuta().isEmpty()) ||
                    (mensaje.getAdjuntoTamano() > 0);
            if (soloAdjuntos && !tieneAdjunto) continue;

            StringBuilder linea =  new StringBuilder();
            linea.append(mensaje.getRemitente()).append(": ").append(mensaje.getMensaje());
            if (tieneAdjunto){
                File f = mensaje.getAdjuntoRuta() != null ? FileManager.getMediaPath(mensaje.getAdjuntoRuta()).toFile() : null;
                boolean existe = f != null && f.exists();
                String nombreAdj = mensaje.getAdjuntoNombre() != null && !mensaje.getAdjuntoNombre().isEmpty()
                        ? mensaje.getAdjuntoNombre()
                        : (mensaje.getAdjuntoRuta() != null ? mensaje.getAdjuntoRuta() : "adjunto");
                linea.append(" [Adjunto: ").append(nombreAdj);
                if (!existe) linea.append(" - NO ENCONTRADO");
                linea.append("]");

                // acumular adjuntos para la lista
                adjuntosConversacion.add(nombreAdj);
            }
            linea.append("\n");
            txtChat.appendText(linea.toString());
        }
        // Actualizar lista de adjuntos de la conversación
        if (listaAdjuntos != null){
            listaAdjuntos.getItems().setAll(adjuntosConversacion);
        }
        // Scroll automatico
        txtChat.selectPositionCaret(txtChat.getLength());
        txtChat.deselect();
    }


    /**
     * Cierra la sesión actual y vuelve a la pantalla de inicio.
     * @param actionEvent evento del botón Cerrar sesión.
     */
    public void cerrarSesion(ActionEvent actionEvent) {
        Sesion.getInstancia().cerrarSesion();
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }

    @FXML
    /**
     * Envía un mensaje (con o sin adjunto) al usuario seleccionado y actualiza la vista.
     * @param actionEvent evento del botón Enviar.
     */
    public void enviarMensaje(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) return;

        String mensaje = txtMensaje.getText().trim();
        if (mensaje.isEmpty()) return;

        if (adjuntoSeleccionado != null){
            MensajeDAO.enviarMensajeConAdjunto(usuarioLogueado.getNombreUsuario(),usuarioSeleccionado.getNombreUsuario(),mensaje,adjuntoSeleccionado);
        }else {
            MensajeDAO.enviarMensaje(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario(), mensaje);
        }
        mostrarMensajes();
        cargarEstadisticas();
        // Limpiar campos después de enviar
        txtMensaje.clear();
        adjuntoSeleccionado = null;
        if (listaAdjuntos != null) {
            listaAdjuntos.getItems().clear();
        }
        lblEstado.setText("Mensaje enviado");
    }

    /**
     * Calcula y muestra las estadísticas de la conversación actual en las etiquetas del panel lateral.
     * Incluye: total de mensajes, mensajes por usuario y top de palabras más comunes.
     */
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
    /**
     * Exporta la conversación actual a un archivo de texto (.txt).
     * @param actionEvent evento del botón Exportar TXT.
     */
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
    /**
     * Exporta la conversación actual a un archivo CSV (.csv).
     * @param actionEvent evento del botón Exportar CSV.
     */
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
    /**
     * Genera un resumen con estadísticas de la conversación y permite guardarlo como TXT.
     * @param actionEvent evento del botón Generar Resumen.
     */
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

    /**
     * Abre un selector de archivos para elegir un adjunto a enviar, valida tamaño y extensión,
     * y muestra la selección de forma temporal en la lista de adjuntos hasta que se envíe el mensaje.
     */
    private void seleccionarAdjunto(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo Adjunto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Permitidos", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.pdf", "*.txt", "*.docx", "*.xlsx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File archivoElegido = fileChooser.showOpenDialog(btnAdjuntar.getScene().getWindow());
        if (archivoElegido == null) return;

        long tamañoMaximo = 10L * 1024 * 1024; //Esta operación calcula el número total de bytes en 10 Megabytes: 10×1024×1024=10.485.760 bytes.
        List<String> extensionesPermitidos = Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".pdf", ".txt", ".docx", ".xlsx");
        if (!FileManager.validarArchivo(archivoElegido, tamañoMaximo, extensionesPermitidos)){
            lblEstado.setText("Error: Archivo Adjunto no valido");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }
        // Guardar selección en el estado del controlador
        adjuntoSeleccionado = archivoElegido;
        // Mostrar en la lista de adjuntos (selección temporal antes de enviar)
        listaAdjuntos.getItems().setAll(archivoElegido.getName());
        lblEstado.setText("Archivo Adjunto: " + archivoElegido.getName());
    }

    /**
     * Elimina la selección de adjunto actual antes de enviar el mensaje
     * y limpia la lista visual de adjuntos.
     */
    private void quitarAdjunto(){
        adjuntoSeleccionado = null;
        listaAdjuntos.getItems().clear();
        lblEstado.setText("Adjunto quitado.");
    }

    /**
     * Intenta abrir el archivo adjunto actualmente seleccionado usando la aplicación predeterminada del sistema.
     * Muestra un mensaje de estado si no hay adjunto seleccionado o si ocurre un error al abrirlo.
     */
    private void abrirAdjunto(){
        if (adjuntoSeleccionado != null){
            boolean exitoso = FileManager.abrirArchivo(adjuntoSeleccionado);
            if (!exitoso){
                lblEstado.setText("No se pudo abrir el adjunto seleccionado.");
            }
        }else {
            lblEstado.setText("No hay adjunto seleccionado.");
        }
    }


    @FXML
    /**
     * Permite al usuario exportar el archivo adjunto seleccionado a una CARPETA de destino,
     * utilizando FileManager.exportarArchivo(origen, destinoDir).
     */
    private void exportarAdjunto(){
        String seleccionado = listaAdjuntos != null ? listaAdjuntos.getSelectionModel().getSelectedItem() : null;

        if (seleccionado == null){
            lblEstado.setText("Error: Selecciona un adjunto de la lista.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        // Intentar localizar el archivo adjunto dentro de la conversación (en media/)
        File archivoOrigen = null;
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
        for (Mensaje m : mensajes.getMensajeList()){
            if (m.getAdjuntoNombre() != null && m.getAdjuntoNombre().equals(seleccionado) && m.getAdjuntoRuta() != null){
                File posible = FileManager.getMediaPath(m.getAdjuntoRuta()).toFile();
                if (posible.exists()){
                    archivoOrigen = posible;
                    break;
                }
            }
        }
        // Si no está en media, tal vez es un adjunto aún no enviado: usar archivo local seleccionado
        if (archivoOrigen == null && adjuntoSeleccionado != null && seleccionado.equals(adjuntoSeleccionado.getName())){
            archivoOrigen = adjuntoSeleccionado;
        }

        if (archivoOrigen == null || !archivoOrigen.exists()){
            lblEstado.setText("Error: Archivo de origen no encontrado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar Carpeta de Destino para Exportar Adjunto");

        File destino = directoryChooser.showDialog(getStage());

        if (destino != null){
            boolean exito = FileManager.exportarArchivo(archivoOrigen,destino);

            if (exito){
                lblEstado.setText("Adjunto exportado con éxito a: " + destino.getAbsolutePath());
                lblEstado.setStyle("-fx-text-fill: green;");
            }else  {
                lblEstado.setText("Error al exportar adjunto.");
                lblEstado.setStyle("-fx-text-fill: red;");
            }
        }else {
            lblEstado.setText("Exportación cancelada.");
            lblEstado.setStyle("-fx-text-fill: gray;");
        }
    }
    }
