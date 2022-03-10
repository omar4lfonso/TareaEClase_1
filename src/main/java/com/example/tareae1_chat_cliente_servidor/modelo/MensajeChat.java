package com.example.tareae1_chat_cliente_servidor.modelo;

import java.io.Serializable;

public class MensajeChat implements Serializable {
    // Estos son los diferentes mensajes enviados por el cliente
    // QUIENESTA para recibir la lista de usuarios conectados
    // MENSAJE un mensaje ordianario
    // LOGOUT para desconectarse del servidor
    public static final int QUIENESTA = 0, MENSAJE = 1, LOGOUT = 2;
    private int tipo;
    private String mensaje;

    // contructor
    public MensajeChat(int tipo, String mensaje){
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    // getters
    public int obtenerTipo() {
        return tipo;
    }

    public String obtenerMensaje(){
        return mensaje;
    }
}
