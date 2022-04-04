module com.example.tareae1_chat_cliente_servidor {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;


    opens com.example.tareae1_chat_cliente_servidor to javafx.fxml;
    exports com.example.tareae1_chat_cliente_servidor;

    opens com.example.tareae1_chat_cliente_servidor.controlador to javafx.fxml;
    exports com.example.tareae1_chat_cliente_servidor.controlador;
}