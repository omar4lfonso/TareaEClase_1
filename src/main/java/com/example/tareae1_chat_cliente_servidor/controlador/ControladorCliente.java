package com.example.tareae1_chat_cliente_servidor.controlador;

import com.example.tareae1_chat_cliente_servidor.modelo.MensajeChat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
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

    /**
     * Metodo utilizado por el boton de login
     */
    public void login(){
        puerto = 1500;
        servidor = txtHostIP.getText();
        System.out.println(servidor);
        nombreUsuario = txtNombreUsuario.getText();
        // probar si se puede iniciar la conexion al servidor
        // si falla no se hace nada
        if(iniciar()){
            return;
        }
        conectado = true;
        btnLogin.setDisable(false);
        btnLogout.setDisable(false);
        txtHostIP.setEditable(false);
    }

    /**
     * Metodo utilizado para desloguearse
     */
    public void logout(){
        if(conectado) {
            MensajeChat msg = new MensajeChat(MensajeChat.LOGOUT, "");
            try{
                sSalida.writeObject(msg);
                txtMsgUsuario.setText("");
                btnLogout.setDisable(false);
                btnLogin.setDisable(true);
                txtNombreUsuario.setEditable(true);
                txtHostIP.setEditable(true);
            }
            catch (IOException e){
                mostrarLog("Excepcion escribiendo al servidor: " + e);
            }
        }
    }

    /**
     * Metodo para enviar mensajes al servidor
     */
    public void enviarMensaje(){
        if(conectado) {
            MensajeChat msg = new MensajeChat(MensajeChat.MENSAJE, txtMsgUsuario.getText());
            try{
                sSalida.writeObject(msg);
                txtMsgUsuario.setText("");
            }
            catch (IOException e){
                mostrarLog("Excepcion escribiendo al servidor: " + e);
            }
        }
    }

    /**
     * Envia los mensajes al servidor si se utilizo la tecla ENTER en vez del boton enviar
     * Este metodo se utiliza por el TextArea txtMensajeUsuario para el evento ENTER (tecla presionada)
     * @param evento : tipo KeyEvent se llama cada vez que una tecla es presionada y verifica si es ENTER
     *               y envia el mensaje en caso de que lo sea.
     */
    public void teclaEnterPresionada(KeyEvent evento){
        if(evento.getCode() == KeyCode.ENTER) {
            enviarMensaje();
            evento.consume();
        }
    }

    /**
     * Inicial la ventana de dialogo
     * @return : envia un bool indicando si la conexion funciono o no
     */
    public boolean iniciar(){
        // Intente conectarse al servidor
        try{
            socket = new Socket(servidor, puerto);
        }
        catch (Exception e){
            mostrarLog("Error en la conexion al servidor: " + e);
        }

        String msg = "Conexion aceptada" + socket.getInetAddress() + ":" + socket.getPort();
        mostrarLog(msg);

        // Creando los dos Data Streams
        try{
            sEntrada = new ObjectInputStream(socket.getInputStream());
            sSalida = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO){
            mostrarLog("Excepcion creando streams de Entrada/Salida" + eIO);
            return false;
        }

        // Crear el hilo que escucha al servidor
        new EscucharServidor().start();

        // Enviar el nombre de usuario al servidor. Este es el unico mensaje que se envia
        // como String. Todos los otros mensajes son de chat.
        try{
            sSalida.writeObject(nombreUsuario);
        }
        catch (IOException eIO){
            mostrarLog("Exepcion de login: " + eIO);
            desconectar();
            return false;
        }

        // Exito: se le informa a la llamada que funciono.
        return true;
    }

    /**
     * Sirve para adjuntar las acciones al log del servidor
     * @param msg
     */
    private void mostrarLog(String msg) {
        txtAreaMensajesServidor.appendText(msg + "\n"); // Adjuntar mensaje al area de chat del servidor
    }

    /**
     * Cuando algo sale mal
     * Cerrar los streams de Entrada/Salida y desconectar
     */
    private void desconectar() {
        try {
            if(sEntrada != null) sEntrada.close();
        }catch (IOException e) {} // sin accion correspondiente

        try{
            if(sSalida != null) sSalida.close();
        }catch (IOException e) {} // sin accion correspondiente

        try{
            if(socket != null) socket.close();
        }catch (IOException e) {} // sin accion correspondiente

        // Informar al GUI
        conexionFallida();
    }

    public void conexionFallida(){
        btnLogin.setDisable(false);
        btnLogout.setDisable(true);
        // permitir que el usuario cambie el Host
        txtHostIP.setEditable(true);
        // se debe evitar la reaccion de los otros modulos
        conectado = false;
    }

    /**
     * Esta clase permite esperar por los mensajes que provienen del servidor y los adjunta al area de texto
     */
    class EscucharServidor extends Thread {
        public void correr(){
            usuarios = FXCollections.observableArrayList();
            listaUsuarios.setItems(usuarios);
            while (true) {
                try {
                    String msg = (String) sEntrada.readObject();
                    String[] msgSeparado = msg.split(":");

                    if(msgSeparado[1].equals("QUIENESTA")){
                        Platform.runLater(() -> {
                            usuarios.add(msgSeparado[0]);
                        });
                    } else if(msgSeparado[1].equals("REMOVER")) {
                        Platform.runLater(() -> {
                            usuarios.remove(msgSeparado[0]);
                        });
                    } else {
                        txtAreaMensajesServidor.appendText(msg);
                    }
                }
                // no sucede con un objeto String pero aun asi debe realizarce el catch
                catch (IOException e) {
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
