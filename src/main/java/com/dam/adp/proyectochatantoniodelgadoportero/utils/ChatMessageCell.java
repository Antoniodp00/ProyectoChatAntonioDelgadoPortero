package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class ChatMessageCell extends ListCell<Mensaje> {
    private final HBox root = new HBox();
    private final VBox bubble = new VBox();
    private final Label lblRemitente = new Label();
    private final Label lblMensaje = new Label();
    private final Label lblHora = new Label();
    private final Hyperlink linkAdjunto = new Hyperlink();
    private final ImageView imgThumb = new ImageView();

    private final Supplier<String> loggedUsernameSupplier;
    private final Label statusLabel;

    /**
     * Constructor para ChatMessageCell.
     * @param loggedUsernameSupplier Un proveedor para obtener el nombre de usuario logueado.
     * @param statusLabel Una etiqueta para mostrar mensajes de estado.
     */
    public ChatMessageCell(Supplier<String> loggedUsernameSupplier, Label statusLabel) {
        super();
        this.loggedUsernameSupplier = loggedUsernameSupplier;
        this.statusLabel = statusLabel;

        getStyleClass().add("list-cell");
        bubble.getStyleClass().add("message-bubble");
        lblHora.getStyleClass().add("timestamp");

        lblRemitente.setStyle("-fx-font-weight: bold;");
        lblMensaje.setWrapText(true);
        lblHora.setWrapText(true);

        // Layout de la burbuja
        bubble.setSpacing(4);
        bubble.setPadding(new Insets(10, 15, 10, 15));
        bubble.getChildren().addAll(lblRemitente, lblMensaje, lblHora);
        bubble.setMaxWidth(400);  // Controlar ancho máximo de burbuja
        bubble.setFillWidth(true);

        // Layout del contenedor principal
        root.setSpacing(8);
        root.getChildren().add(bubble);
        root.setFillHeight(false);

        HBox.setHgrow(bubble, Priority.ALWAYS);

        listViewProperty().addListener((obs, oldLv, newLv) -> {
            if (newLv != null) {
                lblMensaje.maxWidthProperty().bind(newLv.widthProperty().subtract(100));
            }
        });

        // Imagen miniatura
        imgThumb.setFitWidth(120);
        imgThumb.setPreserveRatio(true);
        imgThumb.setSmooth(true);

        linkAdjunto.setVisible(false);
        imgThumb.setVisible(false);
    }

    /**
     * Actualiza el elemento de la celda con un nuevo mensaje.
     * @param item El mensaje a mostrar en la celda.
     * @param empty Si la celda está vacía.
     */
    @Override
    protected void updateItem(Mensaje item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            bubble.getStyleClass().removeAll("own-message", "other-message");
            return;
        }

        String loggedUser = (loggedUsernameSupplier != null) ? loggedUsernameSupplier.get() : null;
        boolean esPropio = loggedUser != null && item.getRemitente() != null && item.getRemitente().equals(loggedUser);

        // Alinear y aplicar estilo de burbuja
        bubble.getStyleClass().removeAll("own-message", "other-message");
        if (esPropio) {
            bubble.getStyleClass().add("own-message");
            root.setAlignment(Pos.CENTER_RIGHT);
        } else {
            bubble.getStyleClass().add("other-message");
            root.setAlignment(Pos.CENTER_LEFT);
        }

        // Contenido del mensaje
        lblRemitente.setText(item.getRemitente() != null ? item.getRemitente() : "");
        lblMensaje.setText(item.getMensaje() != null ? item.getMensaje() : "");

        // Hora
        if (item.getFecha() != null) {
            lblHora.setText(item.getFecha().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            lblHora.setText("");
        }

        // Adjuntos
        if (item.getAdjuntoRuta() != null && !item.getAdjuntoRuta().isEmpty()) {
            File f = new File(FileManager.getRutaMedia(item.getAdjuntoRuta()));
            String nombreAdj = (item.getAdjuntoNombre() != null && !item.getAdjuntoNombre().isEmpty())
                    ? item.getAdjuntoNombre() : item.getAdjuntoRuta();

            // Adjuntar enlace
            if (!bubble.getChildren().contains(linkAdjunto)) {
                bubble.getChildren().add(2, linkAdjunto);
            }
            linkAdjunto.setText("📎 " + nombreAdj);
            linkAdjunto.setOnAction(e -> {
                if (f.exists()) {
                    FileManager.abrirArchivo(f);
                } else if (statusLabel != null) {
                    statusLabel.setText("Adjunto no encontrado");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            });
            linkAdjunto.setVisible(true);

            // Si es imagen, mostrar miniatura
            String tipo = item.getAdjuntoTipo() != null ? item.getAdjuntoTipo().toLowerCase() : "";
            boolean esImagen = tipo.endsWith("png") || tipo.endsWith("jpg") || tipo.endsWith("jpeg")
                    || tipo.endsWith("gif") || tipo.endsWith("bmp");
            if (esImagen && f.exists()) {
                try {
                    Image img = new Image(f.toURI().toString(), 240, 0, true, true);
                    imgThumb.setImage(img);
                    if (!bubble.getChildren().contains(imgThumb)) {
                        bubble.getChildren().add(2, imgThumb);
                    }
                    imgThumb.setVisible(true);
                } catch (Exception ex) {
                    imgThumb.setVisible(false);
                }
            } else {
                imgThumb.setVisible(false);
                bubble.getChildren().remove(imgThumb);
            }
        } else {
            linkAdjunto.setVisible(false);
            bubble.getChildren().remove(linkAdjunto);
            imgThumb.setVisible(false);
            bubble.getChildren().remove(imgThumb);
        }

        setGraphic(root);
    }
}
