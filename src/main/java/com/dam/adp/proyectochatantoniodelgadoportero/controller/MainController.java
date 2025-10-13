package com.dam.adp.proyectochatantoniodelgadoportero.controller;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.MensajeDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.DAO.UsuarioDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Sesion;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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
    public void initialize(){

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

           }
       });
    }

    public void mostrarMensajes(){
       if (usuarioSeleccionado == null)return;

       txtChat.clear();
        Mensajes mensajes = MensajeDAO.listarMensajesEntre(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario());

        for (Mensaje mensaje : mensajes.getMensajeList()){
            txtChat.appendText(mensaje.getRemitente()+": "+mensaje.getMensaje() + "\n");
        }
    }



    public void cerrarSesion(ActionEvent actionEvent) {
    }

    public void generarResumen(ActionEvent actionEvent) {
    }
@FXML
    public void enviarMensaje(ActionEvent actionEvent) {
       if(usuarioSeleccionado == null)return;

       String mensaje = txtMensaje.getText().trim();

       MensajeDAO.enviarMensaje(usuarioLogueado.getNombreUsuario(), usuarioSeleccionado.getNombreUsuario(), mensaje);

       mostrarMensajes();
       txtMensaje.clear();

    }
}
