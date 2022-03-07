package com.example.tareae1_chat_cliente_servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class ConexionRed {

    private hiloConexion hConexion = new hiloConexion();
    private Consumer<Serializable> alRecibirLlamada;

    public ConexionRed(Consumer<Serializable> alRecibirLlamada){
        this.alRecibirLlamada = alRecibirLlamada;
        hConexion.setDaemon(true);
    }

    public void iniciarConexion() throws IOException{
        hConexion.start();
    }

    public void enviarMsg(Serializable msg) throws IOException{
        hConexion.salida.writeObject(msg);
    }

    public void cerrarConexion() throws IOException{
        hConexion.socket.close();
    }

    protected abstract boolean esServidor();
    protected abstract String obtenerIP();
    protected abstract int obtenerPuerto();

    public class hiloConexion extends Thread{

        private Socket socket;
        private ObjectOutputStream salida;


        @Override
        public void run(){
            try{
                ServerSocket servidor = esServidor() ? new ServerSocket(obtenerPuerto()) : null;
                Socket socket = esServidor() ? servidor.accept() : new Socket(obtenerIP(), obtenerPuerto());
                ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

                this.socket = socket;
                this.salida = salida;
                // Ayuda a enviar mensajes mas rapido, sin tener que esperar a que el buffer se llene
                socket.setTcpNoDelay(true);

                // Usar un bucle infinito no es la mejor opcion pero solo se estan recibiendo mensajes y enviado
                // por lo que en esta app no es tanto problema.
                while(true){
                    Serializable datos = (Serializable) entrada.readObject();
                    alRecibirLlamada.accept(datos);
                }
            }
            catch (Exception e){
                alRecibirLlamada.accept("Conexion Finalizada!");
            }

        }
    }
}
