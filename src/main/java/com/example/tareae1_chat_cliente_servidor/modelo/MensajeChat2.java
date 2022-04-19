package com.example.tareae1_chat_cliente_servidor.modelo;

import java.io.Serializable;

public class MensajeChat2 implements Serializable {
    // Estos son los diferentes mensajes enviados por el cliente
    // QUIENESTA para recibir la lista de usuarios conectados
    // MENSAJE un mensaje ordianario
    // LOGOUT para desconectarse del servidor
    private int tipo;
    private int mensaje;

    // contructor
    public MensajeChat2(int tipo, int mensaje){
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    // getters
    public int obtenerTipo() {
        return tipo;
    }

    public int obtenerMensaje(){
        return mensaje;
    }
}
