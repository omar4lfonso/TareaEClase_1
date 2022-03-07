package com.example.tareae1_chat_cliente_servidor;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 */
public class Cliente extends  ConexionRed {

    private String ip;
    private int port;

    public Cliente(String ip, int port, Consumer<Serializable> alRecibirLlamada) {
        super(alRecibirLlamada);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean esServidor(){
        return false;
    }

    @Override
    protected String obtenerIP(){
        return ip;
    }

    @Override
    protected int obtenerPuerto(){
        return port;
    }

}
