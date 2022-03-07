package com.example.tareae1_chat_cliente_servidor;

import javafx.application.Application;
/*import javafx.fxml.FXMLLoader;*/
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatAplicacion extends Application {

    private TextArea messages = new TextArea();

    private Parent createContent(){
        messages.setPrefHeight(550);
        TextField input = new TextField();

        VBox root =  new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(createContent()));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}