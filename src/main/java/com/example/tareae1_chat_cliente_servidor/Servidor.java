package com.example.tareae1_chat_cliente_servidor;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 */
public class Servidor extends  ConexionRed {

    private int port;

    public Servidor(int port, Consumer<Serializable> alRecibirLlamada) {
        super(alRecibirLlamada);
        this.port = port;
    }

    @Override
    protected boolean esServidor(){
        return true;
    }

    @Override
    protected String obtenerIP() {
        return null;
    }

    @Override
    protected int obtenerPuerto(){
        return port;
    }

}
