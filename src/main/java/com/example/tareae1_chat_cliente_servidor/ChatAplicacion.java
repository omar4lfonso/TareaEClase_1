package com.example.tareae1_chat_cliente_servidor;

import com.example.tareae1_chat_cliente_servidor.controlador.ControladorCliente;
import com.example.tareae1_chat_cliente_servidor.controlador.ControladorServidor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ChatAplicacion extends Application {

    private Stage stage;
    private VBox windowLayout;

    private boolean esServidor = false;

    private TextArea mensajes = new TextArea();

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.stage.setTitle(esServidor ? "Chat Servidor" : "Chat App");

        crearContenido();
    }

    private void crearContenido() throws IOException {

        // Cargar los fmxl que contienen el GUI ya sea Cliente o Servidor.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatAplicacion.class.getResource(esServidor ? "ServidorGUI.fxml" : "ClienteGUI.fxml"));

        if(esServidor == false){
            loader.setController(new ControladorCliente());

            windowLayout = (VBox) loader.load();

            Scene scene = new Scene(windowLayout);
            stage.setScene(scene);
            stage.show();
        }
        else{
            ControladorServidor controladorServidor = new ControladorServidor();
            loader.setController(controladorServidor);

            windowLayout = (VBox) loader.load();

            Scene scene = new Scene(windowLayout);
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    // Deben eliminarse los hilos del servidor
                    // en caso de que el usuario decida cerrarlo.
                    if (controladorServidor.servidor != null) {
                        controladorServidor.servidor.detener();
                        controladorServidor.servidor = null;
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        launch();
    }
}