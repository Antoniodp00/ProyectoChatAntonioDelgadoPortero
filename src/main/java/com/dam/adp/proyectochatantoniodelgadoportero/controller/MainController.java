package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.MensajeDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import com.dam.adp.proyectochatantoniodelgadoportero.network.Cliente;
import com.dam.adp.proyectochatantoniodelgadoportero.network.EstadoRed; // Importar el nuevo enum
import com.dam.adp.proyectochatantoniodelgadoportero.utils.ChatMessageCell;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.FileManager;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.StreamUtils;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    // ... otros campos ...
    @FXML
    private Label lblEstadoRed; // INICIO: Campo para el nuevo Label de estado de red

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
    private Cliente cliente;

    @FXML
    public void initialize() {
        if (lvChat != null) {
            lvChat.setCellFactory(list -> new ChatMessageCell(() -> usuarioLogueado != null ? usuarioLogueado.getNombreUsuario() : null, lblEstado));
        }

        usuarioLogueado = Sesion.getInstancia().getUsuario();

        // --- INICIO: CÓDIGO MODIFICADO PARA LA RED ---
        /**
         * Inicializa el cliente de red con dos callbacks:
         * 1. Para procesar mensajes entrantes.
         * 2. Para actualizar el indicador de estado de la red.
         */
        cliente = new Cliente(
            mensaje -> Platform.runLater(() -> recibirMensajeDelServidor(mensaje)),
            estado -> Platform.runLater(() -> actualizarEstadoRed(estado))
        );
        cliente.conectar();
        // --- FIN: CÓDIGO MODIFICADO PARA LA RED ---

        ObservableList<Usuario> usuariosObservableList = FXCollections.observableArrayList(UsuarioDAO.leerUsuarios().getLista());
        usuariosObservableList.removeIf(u -> u != null && u.getNombreUsuario().equals(usuarioLogueado.getNombreUsuario()));
        listaUsuarios.setItems(usuariosObservableList);

        listaUsuarios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                usuarioSeleccionado = newValue;
                lblUsuarioChat.setText("Chat con: " + usuarioSeleccionado.getNombreUsuario());
                mostrarMensajes();
                cargarEstadisticas();
            }
        });

        btnAdjuntar.setOnAction(e -> seleccionarAdjunto());
        btnAbrirAdjunto.setOnAction(e -> abrirAdjunto());
        btnExportarAdjunto.setOnAction(e -> exportarAdjunto());
        chkSoloAdjuntos.setOnAction(e -> mostrarMensajes());
        btnExportarZip.setOnAction(e -> exportarZip());

        txtMensaje.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                enviarMensaje(null);
                event.consume();
            }
        });
    }

    // ... (enviarMensaje y recibirMensajeDelServidor sin cambios) ...

    // --- INICIO: NUEVO MÉTODO PARA ACTUALIZAR EL INDICADOR DE RED ---
    /**
     * Actualiza la etiqueta de estado de la red según el estado recibido del cliente.
     * Cambia el texto y el color para reflejar si la conexión está activa, inactiva o ha fallado.
     * @param estado El nuevo estado de la red (CONECTADO, DESCONECTADO, ERROR).
     */
    private void actualizarEstadoRed(EstadoRed estado) {
        if (lblEstadoRed == null) return;

        switch (estado) {
            case CONECTADO:
                lblEstadoRed.setText("Estado: Conectado");
                lblEstadoRed.setStyle("-fx-text-fill: #2e8b57; -fx-font-weight: bold;"); // Verde
                break;
            case DESCONECTADO:
                lblEstadoRed.setText("Estado: Desconectado");
                lblEstadoRed.setStyle("-fx-text-fill: #a9a9a9; -fx-font-weight: normal;"); // Gris
                break;
            case ERROR:
                lblEstadoRed.setText("Estado: Error de red");
                lblEstadoRed.setStyle("-fx-text-fill: #b22222; -fx-font-weight: bold;"); // Rojo
                break;
        }
    }
    // --- FIN: NUEVO MÉTODO ---

    public void cerrarSesion(ActionEvent actionEvent) {
        if (cliente != null) {
            cliente.desconectar();
        }
        Sesion.getInstancia().cerrarSesion();
        Utils.cambiarEscena("/com/dam/adp/proyectochatantoniodelgadoportero/landingPageView.fxml");
    }

    // ... (El resto de los métodos permanecen sin cambios) ...
    @FXML
    public void enviarMensaje(ActionEvent actionEvent) {
        boolean puedeEnviar = (usuarioSeleccionado != null);
        if (puedeEnviar) {
            String mensaje = txtMensaje.getText().trim();
            if (!mensaje.isEmpty()) {
                Mensaje mensajeAEnviar = new Mensaje(
                        usuarioLogueado.getNombreUsuario(),
                        usuarioSeleccionado.getNombreUsuario(),
                        mensaje,
                        LocalDateTime.now()
                );

                cliente.enviarMensaje(mensajeAEnviar);
                MensajeDAO.enviarMensaje(mensajeAEnviar.getRemitente(), mensajeAEnviar.getDestinatario(), mensajeAEnviar.getMensaje());

                txtMensaje.clear();
                adjuntoSeleccionado = null;
                if (listaAdjuntos != null) {
                    listaAdjuntos.getItems().clear();
                }
                lblEstado.setText("Mensaje enviado");
            }
        }
    }

    private void recibirMensajeDelServidor(Mensaje mensaje) {
        if (mensaje == null || usuarioSeleccionado == null) {
            return;
        }

        String remitente = mensaje.getRemitente();
        String destinatario = mensaje.getDestinatario();
        String usuarioActual = usuarioLogueado.getNombreUsuario();
        String otroUsuario = usuarioSeleccionado.getNombreUsuario();

        boolean esMensajeDeEstaConversacion = (remitente.equals(usuarioActual) && destinatario.equals(otroUsuario)) ||
                                              (remitente.equals(otroUsuario) && destinatario.equals(usuarioActual));

        if (esMensajeDeEstaConversacion) {
            lvChat.getItems().add(mensaje);
            lvChat.scrollTo(lvChat.getItems().size() - 1);
            cargarEstadisticas();
        }
    }

    private Stage getStage() {
        if (stage == null && lblEstado != null) {
            stage = (Stage) lblEstado.getScene().getWindow();
        }
        return stage;
    }

    private void mostrarMensajes() {
        if (usuarioSeleccionado != null) {
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(
                    usuarioLogueado.getNombreUsuario(),
                    usuarioSeleccionado.getNombreUsuario()
            );

            boolean soloAdjuntos = (chkSoloAdjuntos != null) && chkSoloAdjuntos.isSelected();

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
    public void exportarTxt(ActionEvent actionEvent) {
        if (usuarioSeleccionado == null) {
            lblEstado.setText("Error: No hay usuario seleccionado.");
            lblEstado.setStyle("-fx-text-fill: red;");
        } else {
            Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());
            if (!mensajes.getMensajeList().isEmpty()) {
                String nombreArchivo = usuarioLogueado.getNombreUsuario() + "-" + usuarioSeleccionado.getNombreUsuario() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                File file = mostrarDialogoExportar("Exportar a TXT", nombreArchivo, new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));
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
        File file = mostrarDialogoExportar("Exportar a CSV", nombreArchivo, new FileChooser.ExtensionFilter("Archivo de Texto (*.csv)", "*.csv"));
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
        String nombreSugerido = "Stats_" + usuarioLogueado.getNombreUsuario() + usuarioSeleccionado.getNombreUsuario() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        File file = mostrarDialogoExportar("Guardar Estadísticas (TXT)", nombreSugerido, new FileChooser.ExtensionFilter("Archivo de Texto (*.txt)", "*.txt"));
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

    private ArrayList<String> generaEstadisticasTexto() {
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

    private File localizarArchivoAdjunto(String nombreAdjunto) {
        if (nombreAdjunto == null || usuarioSeleccionado == null || usuarioLogueado == null) {
            return null;
        }
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(
                usuarioLogueado.getNombreUsuario(),
                usuarioSeleccionado.getNombreUsuario()
        );
        for (Mensaje m : mensajes.getMensajeList()) {
            if (m.getAdjuntoNombre() != null && m.getAdjuntoNombre().equals(nombreAdjunto) && m.getAdjuntoRuta() != null) {
                File posible = new File(FileManager.getRutaMedia(m.getAdjuntoRuta()));
                if (posible.exists()) {
                    return posible;
                }
            }
        }
        if (adjuntoSeleccionado != null && nombreAdjunto.equals(adjuntoSeleccionado.getName())) {
            return adjuntoSeleccionado;
        }
        return null;
    }

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

    private void exportarZip() {
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

    private File mostrarDialogoExportar(String titulo, String nombreArchivo, FileChooser.ExtensionFilter filtro) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombreArchivo + ".extension");
        if (filtro != null) {
            fileChooser.getExtensionFilters().add(filtro);
        }
        return fileChooser.showSaveDialog(getStage());
    }
}
