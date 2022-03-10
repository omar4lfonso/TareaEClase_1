package com.example.tareae1_chat_cliente_servidor.controlador;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ControladorServidor {
    @FXML
    private TextArea txtAreaMensajesServidor;
    @FXML
    private TextArea txtAreaLogEventos;
    @FXML
    private ListView<String> listaUsuariosConectados;
    @FXML
    private Button btnInicioServidor;
    @FXML
    private Button btnDetenerServidor;

    public Servidor servidor;

    private ObservableList<String> usuarios;

    public void iniciarServidor(){
        // crear nuevo Servidor
        servidor = new Servidor(1500, (ControladorServidor) this);
        usuarios = FXCollections.observableArrayList();
        listaUsuariosConectados.setItems(usuarios);
        new ServidorEjecutando().start();
        btnInicioServidor.setDisable(true);
        btnDetenerServidor.setDisable(false);
    }

    /**
     * Esta funcion se encarga de detener el servidor
     */
    public void detenerServidor(){
        if (servidor != null){
            servidor.detener();
            btnDetenerServidor.setDisable(true);
            btnInicioServidor.setDisable(false);
            listaUsuariosConectados.setItems(null);
            servidor = null;
            return;
        }
    }

    /**
     * Un hilo para correr el servidor
     */
    class ServidorEjecutando extends Thread {
        public void run() {
            servidor.iniciar();         // should execute until if fails
            // the server failed
            agregarEvento("El servidor se ha detenido\n");
            servidor = null;
            usuarios = null;
        }
    }

    public void agregarUsuario(String user) {
        Platform.runLater(() -> {
            usuarios.add(user);
        });
    }

    /**
     * Esta funcion se encarga de agregar los mensajes de historial de acciones al GUI del servidor
     * @param string : mensaje a mostrar
     */
    public void agregarEvento(String string) {
        txtAreaLogEventos.appendText(string);
    }

    public void appendRoom(String messageLf) {
        txtAreaMensajesServidor.appendText(messageLf);
    }

    public void remove(String username) {
        Platform.runLater(() -> {
            usuarios.remove(username);
        });
    }
}
