package com.example.tareae1_chat_cliente_servidor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatAplicacion extends Application {

    private Stage stage;
    private VBox windowLayout;

    private boolean esServidor = false;

    private TextArea mensajes = new TextArea();
    private ConexionRed conexion = esServidor ? crearServidor() : crearCliente();

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.stage.setTitle(esServidor ? "Chat Servidor" : "Chat App");

        /*stage.setScene(new Scene(crearContenido()));
        stage.show();
        stage.setTitle(esServidor ? "Servidor" : "Cliente");*/
    }

    private void crearContenido(){

        // Cargar los fmxl que contienen el GUI ya sea Cliente o Servidor.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatAplicacion.class.getResource(esServidor ? "ServidorGUI.fxml" : "ClienteGUI.fxml"));



        /*mensajes.setPrefHeight(550);
        TextField input = new TextField();
        Button enviarBtn = new Button();
        input.setOnAction(event -> {
            String mensaje = esServidor ? "Servidor: " : "Cliente: ";
            mensaje += input.getText();
            input.clear();

            mensajes.appendText(mensaje + "\n");

            try {
                conexion.enviarMsg(mensaje);
            }
            catch (IOException e) {
                mensajes.appendText("ERROR DE ENVIO!\n");
            }
        });

        enviarBtn.setText("Enviar");

        VBox root =  new VBox(20, mensajes, input, enviarBtn);
        root.setPrefSize(600, 600);
        return root;*/
    }

    @Override
    public void init() throws IOException {
        conexion.iniciarConexion();
    }

    @Override
    public void stop() throws IOException {
        conexion.cerrarConexion();
    }

    private Servidor crearServidor(){
        return new Servidor(55555, data -> {
            Platform.runLater(() -> {
                mensajes.appendText(data.toString() + "\n");
            });
        });
    }

    private Cliente crearCliente(){
        return new Cliente("127.0.0.1", 55555,  data -> {
            Platform.runLater(() -> {
                mensajes.appendText(data.toString() + "\n");
            });
        });
    }

    public static void main(String[] args) {
        launch();
    }
}