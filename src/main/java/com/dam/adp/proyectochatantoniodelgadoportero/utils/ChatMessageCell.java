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
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * Celda personalizada para mostrar mensajes como burbujas con alineación, hora y adjuntos.
 * Esta clase se ha extraído desde MainController para reutilización y limpieza del controlador.
 */
public class ChatMessageCell extends ListCell<Mensaje> {
    private final HBox root = new HBox();
    private final VBox bubble = new VBox();
    private final Label lblRemitenteYTexto = new Label();
    private final Label lblHora = new Label();
    private final Hyperlink linkAdjunto = new Hyperlink();
    private final ImageView imgThumb = new ImageView();

    private final Supplier<String> loggedUsernameSupplier;
    private final Label statusLabel;

    public ChatMessageCell(Supplier<String> loggedUsernameSupplier, Label statusLabel) {
        super();
        this.loggedUsernameSupplier = loggedUsernameSupplier;
        this.statusLabel = statusLabel;

        bubble.getChildren().add(lblRemitenteYTexto);
        bubble.getChildren().add(lblHora);
        bubble.setSpacing(4);
        bubble.setPadding(new Insets(8, 12, 8, 12));

        imgThumb.setFitWidth(120);
        imgThumb.setPreserveRatio(true);
        imgThumb.setSmooth(true);

        linkAdjunto.setVisible(false);
        imgThumb.setVisible(false);

        root.getChildren().add(bubble);
        root.setFillHeight(false);
    }

    @Override
    protected void updateItem(Mensaje item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        String loggedUser = (loggedUsernameSupplier != null) ? loggedUsernameSupplier.get() : null;
        boolean esPropio = loggedUser != null && item.getRemitente() != null && item.getRemitente().equals(loggedUser);

        // Alinear izquierda/derecha
        root.getChildren().clear();
        bubble.getStyleClass().clear();
        bubble.getStyleClass().add("message-bubble");
        if (esPropio) {
            bubble.getStyleClass().add("own-message");
            root.setAlignment(Pos.CENTER_RIGHT);
            root.getChildren().add(bubble);
        } else {
            bubble.getStyleClass().add("other-message");
            root.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(bubble);
        }

        // Contenido principal
        String remitente = item.getRemitente() != null ? item.getRemitente() : "";
        String texto = item.getMensaje() != null ? item.getMensaje() : "";
        lblRemitenteYTexto.setText(remitente + ": " + texto);

        // Hora
        lblHora.getStyleClass().clear();
        lblHora.getStyleClass().add("timestamp");
        if (item.getFecha() != null) {
            lblHora.setText(item.getFecha().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            lblHora.setText("");
        }

        // Adjuntos
        if (item.getAdjuntoRuta() != null && !item.getAdjuntoRuta().isEmpty()) {
            File f = new File(FileManager.getRutaMedia(item.getAdjuntoRuta()));
            String nombreAdj = (item.getAdjuntoNombre() != null && !item.getAdjuntoNombre().isEmpty()) ? item.getAdjuntoNombre() : item.getAdjuntoRuta();

            // Asegurar que los nodos de adjunto están en el bubble (después del texto y hora)
            if (!bubble.getChildren().contains(linkAdjunto)) {
                bubble.getChildren().add(1, linkAdjunto);
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

            // Thumbnail para imágenes
            String tipo = item.getAdjuntoTipo() != null ? item.getAdjuntoTipo().toLowerCase() : "";
            boolean esImagen = tipo.endsWith("png") || tipo.endsWith("jpg") || tipo.endsWith("jpeg") || tipo.endsWith("gif") || tipo.endsWith("bmp");
            if (esImagen && f.exists()) {
                try {
                    Image img = new Image(f.toURI().toString(), 240, 0, true, true);
                    imgThumb.setImage(img);
                    if (!bubble.getChildren().contains(imgThumb)) {
                        bubble.getChildren().add(1, imgThumb);
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
