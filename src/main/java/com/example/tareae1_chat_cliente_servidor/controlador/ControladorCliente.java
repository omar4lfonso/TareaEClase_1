package com.example.tareae1_chat_cliente_servidor.controlador;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ControladorCliente {
    // Implementacion de Java FX
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLogout;
    @FXML
    private TextArea txtAreaMensajesServidor;
    @FXML
    private TextField txtHostIP;
    @FXML
    private TextField txtNombreUsuario;
    @FXML
    private ListView<String> listaUsuarios;
    @FXML
    private TextArea txtMsgUsuario;

    private ObservableList<String> usuarios;

    // Configuracion del servidor
    private boolean conectado;
    private String servidor, nombreUsuario;
    private int puerto;

    // Para I/O
    private ObjectInputStream sEntrada;		// para lectura del socket
    private ObjectOutputStream sSalida;		// para escritura del socket
    private Socket socket;
}
