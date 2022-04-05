package com.example.tareae1_chat_cliente_servidor;

import com.example.tareae1_chat_cliente_servidor.controlador.ControladorServidor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.log4j.Logger;

import java.io.IOException;

public class ChatAplicacion extends Application {
    private static final Logger logger = LogFactory.getLogger();

    private Stage stage;

    private static boolean esServidor = false;

    private String tipoApp = esServidor ? "ServidorGUI.fxml" : "ClienteGUI.fxml";

    private void crearContenido() throws IOException {

        // Cargar los fmxl que contienen el GUI ya sea Cliente o Servidor.

        if(esServidor == false){
            FXMLLoader loader = new FXMLLoader(ChatAplicacion.class.getResource("ClienteGUI.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            logger.info("Esta es la aplicación de Cliente!");
        }
        else{
            FXMLLoader loader = new FXMLLoader(ChatAplicacion.class.getResource("ServidorGUI.fxml"));
            ControladorServidor controladorServidor = new ControladorServidor();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            logger.info("Esta es la aplicación de Servidor!");

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

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.stage.setTitle(esServidor ? "Chat Servidor" : "Chat App");

        crearContenido();
    }
    public static void main(String[] args) {
        launch();
    }
}

