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
import com.dam.adp.proyectochatantoniodelgadoportero.utils.ChatMessageCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    public ListView<String> listaAdjuntos;
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
    private ListView<Mensaje> lvChat;
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

        // Configurar celdas personalizadas para la lista de mensajes
        if (lvChat != null) {
            lvChat.setCellFactory(list -> new ChatMessageCell(() -> usuarioLogueado != null ? usuarioLogueado.getNombreUsuario() : null, lblEstado));
        }

        usuarioLogueado = Sesion.getInstancia().getUsuario();

        ObservableList<Usuario> usuariosObservableList = FXCollections.observableArrayList(UsuarioDAO.leerUsuarios().getLista());

        for (int i = 0; i < usuariosObservableList.size(); i++) {
            Usuario u = usuariosObservableList.get(i);
            if (u != null && u.getNombre().equals(usuarioLogueado.getNombre())) {
                usuariosObservableList.remove(i);
                break;
            }
        }

        listaUsuarios.setItems(usuariosObservableList);

        listaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        usuarioSeleccionado = newValue;
                        lblUsuarioChat.setText("Chat con: " + usuarioSeleccionado.getNombreUsuario());
                        mostrarMensajes();
                        cargarEstadisticas();
                    }
                }
        );

        btnAdjuntar.setOnAction(e -> seleccionarAdjunto());
        btnAbrirAdjunto.setOnAction(e -> abrirAdjunto());
        btnExportarAdjunto.setOnAction(e -> exportarAdjunto());
        chkSoloAdjuntos.setOnAction(e -> mostrarMensajes());
        btnExportarZip.setOnAction(e -> exportarZip());

        //Pulsar Enter para enviar mensaje
        txtMensaje.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                enviarMensaje(null);
                event.consume();
            }
        });
    }

    /**
     * Obtiene y memoriza el Stage actual asociado a la vista.
     * Se usa para abrir diálogos (FileChooser/DirectoryChooser) desde este controlador.
     *
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
        if (usuarioSeleccionado != null) {
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(
                    usuarioLogueado.getNombreUsuario(),
                    usuarioSeleccionado.getNombreUsuario()
            );

            boolean soloAdjuntos = (chkSoloAdjuntos != null) && chkSoloAdjuntos.isSelected();

            // Filtrar si es necesario y recopilar nombres de adjuntos
            List<Mensaje> filtrados = new ArrayList<>();
            List<String> adjuntosConversacion = new ArrayList<>();
            for (Mensaje mensaje : mensajes.getMensajeList()) {
                boolean tieneAdjunto = ((mensaje.getAdjuntoRuta() != null) && !mensaje.getAdjuntoRuta().isEmpty()) || (mensaje.getAdjuntoTamano() > 0);
                if (soloAdjuntos && !tieneAdjunto) {
                    continue;
                }
                filtrados.add(mensaje);
                if (tieneAdjunto) {
                    String nombreAdj = (mensaje.getAdjuntoNombre() != null && !mensaje.getAdjuntoNombre().isEmpty())
                            ? mensaje.getAdjuntoNombre()
                            : ((mensaje.getAdjuntoRuta() != null) ? mensaje.getAdjuntoRuta() : "adjunto");
                    adjuntosConversacion.add(nombreAdj);
                }
            }

            if (lvChat != null) {
                lvChat.setItems(FXCollections.observableArrayList(filtrados));
                if (!filtrados.isEmpty()) {
                    lvChat.scrollTo(filtrados.size() - 1);
                }
            }

            if (listaAdjuntos != null) {
                listaAdjuntos.getItems().setAll(adjuntosConversacion);
            }
        }
    }


    /**
     * Cierra la sesión actual y vuelve a la pantalla de inicio.
     *
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
        boolean puedeEnviar = (usuarioSeleccionado != null);
        if (puedeEnviar) {
            String mensaje = txtMensaje.getText().trim();
            if (!mensaje.isEmpty()) {
                if (adjuntoSeleccionado != null) {
                    MensajeDAO.enviarMensajeConAdjunto(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario(), mensaje, adjuntoSeleccionado);
                } else {
                    MensajeDAO.enviarMensaje(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario(), mensaje);
                }
                mostrarMensajes();
                cargarEstadisticas();
                txtMensaje.clear();
                adjuntoSeleccionado = null;
                if (listaAdjuntos != null) {
                    listaAdjuntos.getItems().clear();
                }
                lblEstado.setText("Mensaje enviado");
            }
        }
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
        } else {
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());

            int totalMensajes = StreamUtils.contarMensajes(mensajes.getMensajeList());
            lblTotalMensajes.setText(String.valueOf(totalMensajes));

            Map<String, Long> porUsuario = StreamUtils.contarMensajesPorUsuario(mensajes.getMensajeList());
            lblMensajesPorUsuario.setText(StreamUtils.formatearConteoUsuario(porUsuario));

            Map<String, Long> topPalabras = StreamUtils.palabraMasComun(mensajes.getMensajeList(), 5);
            lblPalabrasComunes.setText(StreamUtils.formatearTopPalabras(topPalabras));
        }
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
        } else {
            // Obtener todos los mensajes
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
            if (!mensajes.getMensajeList().isEmpty()) {

                String nombreArchivo = usuarioLogueado.getNombreUsuario() + "-" + usuarioSeleccionado.getNombreUsuario() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                File file = mostrarDialogoExportar("Exportar a TXT",nombreArchivo,new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));

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
            } else {
                lblEstado.setText("No hay mensajes para exportar");
            }
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

        String nombreArchivo = usuarioLogueado.getNombreUsuario() + "-" + usuarioSeleccionado.getNombreUsuario() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        File file = mostrarDialogoExportar("Exportar a CSV", nombreArchivo,new FileChooser.ExtensionFilter("Archivo de Texto (*.csv)", "*.csv"));

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

        String nombreSugerido = "Stats_" + usuarioLogueado.getNombreUsuario() + usuarioSeleccionado.getNombreUsuario() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        File file = mostrarDialogoExportar("Guardar Estadísticas (TXT)",nombreSugerido,new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));

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
    private ArrayList<String> generaEstadisticasTexto() {

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
    private void seleccionarAdjunto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo Adjunto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Permitidos", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.pdf", "*.txt", "*.docx", "*.xlsx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File archivoElegido = fileChooser.showOpenDialog(btnAdjuntar.getScene().getWindow());
        if (archivoElegido == null) {
            return;
        }

        long tamañoMaximo = FileManager.TAMAÑO_MAXIMO_BYTES;
        if (!FileManager.validarArchivo(archivoElegido, tamañoMaximo, FileManager.EXTENSIONES_PERMITIDAS)) {
            lblEstado.setText("Error: Archivo Adjunto no valido");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        adjuntoSeleccionado = archivoElegido;

        listaAdjuntos.getItems().setAll(archivoElegido.getName());
        lblEstado.setText("Archivo Adjunto: " + archivoElegido.getName());
    }

    /**
     * Localiza el archivo físico correspondiente al nombre de adjunto seleccionado.
     * Busca primero en los mensajes (carpeta media/ mediante adjuntoRuta) y, si no lo encuentra,
     * comprueba si coincide con el archivo pendiente de envío (adjuntoSeleccionado).
     * @param nombreAdjunto nombre del adjunto seleccionado en la lista
     * @return File encontrado o null si no existe/localiza
     */
    private File localizarArchivoAdjunto(String nombreAdjunto) {
        File archivoEncontrado = null;

        if (nombreAdjunto != null && usuarioSeleccionado != null && usuarioLogueado != null) {
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(
                    usuarioLogueado.getNombreUsuario(),
                    usuarioSeleccionado.getNombreUsuario()
            );

            for (Mensaje m : mensajes.getMensajeList()) {
                if (m.getAdjuntoNombre() != null && m.getAdjuntoNombre().equals(nombreAdjunto) && m.getAdjuntoRuta() != null) {
                    File posible = new File(FileManager.getRutaMedia(m.getAdjuntoRuta()));
                    if (posible.exists()) {
                        archivoEncontrado = posible;
                        break;
                    }
                }
            }

            if (archivoEncontrado == null && adjuntoSeleccionado != null && nombreAdjunto.equals(adjuntoSeleccionado.getName())) {
                archivoEncontrado = adjuntoSeleccionado;
            }
        }

        return archivoEncontrado;
    }

    /**
     * Intenta abrir el archivo adjunto actualmente seleccionado usando la aplicación predeterminada del sistema.
     * Muestra un mensaje de estado si no hay adjunto seleccionado o si ocurre un error al abrirlo.
     */
    private void abrirAdjunto() {
        String seleccionado = (listaAdjuntos != null) ? listaAdjuntos.getSelectionModel().getSelectedItem() : null;
        if (seleccionado == null) {
            lblEstado.setText("No hay adjunto seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }
        File archivoOrigen = localizarArchivoAdjunto(seleccionado);
        if (archivoOrigen == null || !archivoOrigen.exists()) {
            lblEstado.setText("No se pudo localizar el adjunto seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }
        boolean exitoso = FileManager.abrirArchivo(archivoOrigen);
        if (!exitoso) {
            lblEstado.setText("No se pudo abrir el adjunto seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
        }
    }


    @FXML
    /**
     * Permite al usuario exportar el archivo adjunto seleccionado a una CARPETA de destino,
     * utilizando FileManager.exportarArchivo(origen, destinoDir).
     */
    private void exportarAdjunto() {
        String seleccionado = listaAdjuntos != null ? listaAdjuntos.getSelectionModel().getSelectedItem() : null;

        if (seleccionado == null) {
            lblEstado.setText("Error: Selecciona un adjunto de la lista.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        File archivoOrigen = localizarArchivoAdjunto(seleccionado);

        if (archivoOrigen == null || !archivoOrigen.exists()) {
            lblEstado.setText("Error: Archivo de origen no encontrado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar Carpeta de Destino para Exportar Adjunto");

        File destino = directoryChooser.showDialog(getStage());

        if (destino != null) {
            boolean exito = FileManager.exportarArchivo(archivoOrigen, destino);

            if (exito) {
                lblEstado.setText("Adjunto exportado con éxito a: " + destino.getAbsolutePath());
                lblEstado.setStyle("-fx-text-fill: green;");
            } else {
                lblEstado.setText("Error al exportar adjunto.");
                lblEstado.setStyle("-fx-text-fill: red;");
            }
        } else {
            lblEstado.setText("Exportación cancelada.");
            lblEstado.setStyle("-fx-text-fill: gray;");
        }
    }

    /**
     * Exporta la conversación actual a un archivo ZIP.
     * Incluye un fichero de texto con la conversación y adjunta los archivos existentes en la carpeta 'media/'.
     * Valida que haya un usuario seleccionado y que existan mensajes antes de mostrar el diálogo de guardado.
     */
    private void exportarZip() {
        if (usuarioSeleccionado == null){
            lblEstado.setText("Error: No hay usuario seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
        if (mensajes.getMensajeList().isEmpty()) {
            lblEstado.setText("No hay mensajes para exportar");
            return;
        }

        String nombreArchivo = usuarioLogueado.getNombreUsuario() +"-"+usuarioSeleccionado.getNombreUsuario()+ "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        File zipFile = mostrarDialogoExportar("Exportar conversacion a ZIP", nombreArchivo, new FileChooser.ExtensionFilter("Archivo ZIP (*.zip)", "*.zip"));

        if (zipFile == null) {
            lblEstado.setText("Operacion cancelada");
            lblEstado.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            String conversacionTexto = generarTextoConversacion(mensajes.getMensajeList());
            FileManager.crearArchivoZip(zipFile, conversacionTexto, mensajes.getMensajeList());
            lblEstado.setText("Exportado a ZIP con éxito: " + zipFile.getName());
            lblEstado.setStyle("-fx-text-fill: green;");
        } catch (IOException e) {
            lblEstado.setText("Error al crear el archivo ZIP.");
            lblEstado.setStyle("-fx-text-fill: red;");
            log.error("Error al crear el archivo ZIP: {}", zipFile, e);
        }
    }

    /**
     * Genera un único String con el contenido de la conversación. (Este método auxiliar no cambia)
     * @param mensajes Lista de mensajes de la conversación.
     * @return String formateado con la conversación.
     */
    private String generarTextoConversacion(List<Mensaje> mensajes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Conversación entre: ").append(usuarioLogueado.getNombreUsuario()).append(" y ").append(usuarioSeleccionado.getNombreUsuario()).append("\n");
        sb.append("==================================================\n\n");

        for (Mensaje mensaje : mensajes) {
            sb.append(mensaje.getRemitente()).append(" [").append(mensaje.getFecha()).append("]:\n");
            sb.append(mensaje.getMensaje()).append("\n");
            if (mensaje.getAdjuntoNombre() != null && !mensaje.getAdjuntoNombre().isBlank()) {
                sb.append(" -> Adjunto: ").append(mensaje.getAdjuntoNombre()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Crea y muestra un diálogo de guardado para exportar archivos.
     * Configura el título, el nombre de archivo sugerido y aplica el filtro de extensión indicado.
     *
     * @param titulo título del cuadro de diálogo.
     * @param nombreArchivo nombre de archivo sugerido (sin extensión o con ella).
     * @param filtro filtro de extensión (por ejemplo, "Archivo de Texto (*.txt)"). Puede ser null.
     * @return el archivo elegido por el usuario o null si se cancela.
     */
    private File mostrarDialogoExportar(String titulo,String nombreArchivo,FileChooser.ExtensionFilter filtro) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombreArchivo+".extension");
        if (filtro != null) {
            fileChooser.getExtensionFilters().add(filtro);
        }
        return fileChooser.showSaveDialog( getStage());
    }
    
}
