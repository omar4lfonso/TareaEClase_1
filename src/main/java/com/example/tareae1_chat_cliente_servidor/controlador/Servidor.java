package com.example.tareae1_chat_cliente_servidor.controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.tareae1_chat_cliente_servidor.ChatAplicacion;
import com.example.tareae1_chat_cliente_servidor.modelo.MensajeChat;
import com.example.tareae1_chat_cliente_servidor.modelo.MensajeChat2;
import com.example.tareae1_chat_cliente_servidor.modelo.SwitchClases;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Servidor {
    private static Logger logger = Logger.getLogger(ChatAplicacion.class);
    // Debe crearse un ID unico para cada conexion
    private static int idUnico;
    // un ArrayList para guardar la lista de clientes conectados
    private ArrayList<ClientThread> clientesConectados;
    // si me encuentro en un GUI
    private ControladorServidor controladorServidor;
    // para mostrar fecha y hora
    private SimpleDateFormat sdf;
    // Debe identificarse el puerto al que se desea escuchar
    private int puerto;
    // Esta variable se usa para apagar el servidor
    private boolean continuarServidor;

    public Servidor(int puerto){
        this.puerto = puerto;
    }

    public Servidor(int puerto, ControladorServidor controladorServidor){
        this.controladorServidor = controladorServidor;
        this.puerto = puerto;

        // para mostrar la fecha
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList para la lista de clientes
        clientesConectados = new ArrayList<ClientThread>();
    }

    public void iniciar(){
        continuarServidor = true;
        // Crear un socket servidor y esperar solicitudes de conexion
        try{
            // Socket empleado por el servidor
            ServerSocket serverSocket = new ServerSocket(puerto);

            // Ciclo infinito para esperar por conexiones
            while(continuarServidor){
                // Escribir mensaje explicando que se esta esperando
                mostrarLog("Servidor esperando por mensajes en el puerto " + puerto + ".");

                Socket socket = serverSocket.accept(); // aceptar conexion
                // si se requiere detener la conexion
                if(!continuarServidor){
                    break;
                }
                ClientThread t = new ClientThread(socket); // crear un hilo del cliente
                clientesConectados.add(t); // guardar en la lista de clientes conectados

                t.start();
            }
            // En caso de que se solicite detener
            try {
                serverSocket.close();
                for(int i = 0; i < clientesConectados.size(); ++i) {
                    ClientThread tc = clientesConectados.get(i);
                    try {
                        tc.sEntrada.close();
                        tc.sSalida.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                        // not much I can do
                    }
                }
            }
            catch(Exception e) {
                mostrarLog("Excepcion cerrando servidor y clientes: " + e);
                logger.fatal("Excepcion cerrando servidor y clientes: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + "Excepcion en un nuevo ServerSocket: " + e + "\n";
            mostrarLog(msg);
            logger.fatal("Excepci??n en un nuevo ServerSocket: " + e);
        }
    }

    /**
     * Funci??n para el GUI para detener el servidor
     */
    public void detener(){
        continuarServidor = false;
        // conectarse a simismo como cliente para salir

        try {
            new Socket("localhost", puerto);
        }
        catch (Exception e){
            // sin accion
        }
    }

    /**
     * Esta funcion muestra el log de los eventos
     * @param msg : String a mostrar en el event log
     */
    private synchronized void mostrarLog(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        controladorServidor.agregarEvento(time + "\n");
    }

    /**
     * Esta funcion se encarga de difundir un mensaje a todos los clientes
     * @param mensaje
     */
    private synchronized void difundirMsg(String mensaje){
        // agregar fecha y nueva linea al mensaje
        String fecha = sdf.format(new Date());
        String mensajeLf;
        if(mensaje.contains("QUIENESTA") || mensaje.contains("REMOVE")){
            mensajeLf = mensaje;
        } else {
            mensajeLf = fecha + " " + mensaje + "\n";
            controladorServidor.appendRoom(mensajeLf); // agregar en la ventana de cuarto
        }

        // se realiza un ciclo en orden inverso en caso de que se necesite remover un Cliente
        // porque haberse desconectado
        for (int i = clientesConectados.size(); i > 0; --i){
            ClientThread ct = clientesConectados.get(i-1);
            // intente escribir al cliente, si este falla, remover de la lista
            if(!ct.writeMsg(mensajeLf)){
                clientesConectados.remove(i);
                controladorServidor.remove(ct.nombreUsuario);
                mostrarLog("Cliente desconectado: " + ct.nombreUsuario);
            }
        }
    }

    /**
     * Esta funcion se encarga de eliminar al cliente cuando este utiliza el boton de LOGOUT
     * @param id : identificador del cliente
     */
    synchronized void remove(int id){
        // Buscar el ID del cliente
        for(int i = 0; i < clientesConectados.size(); ++i){
            ClientThread ct = clientesConectados.get(i);
            if(ct.id == id){
                controladorServidor.remove(ct.nombreUsuario);
                ct.writeMsg(ct.nombreUsuario + ":REMOVE");
                clientesConectados.remove(i);
                return;
            }
        }
    }

    class ClientThread extends Thread {
        // el socket donde se lee/escribe
        Socket socket;
        ObjectInputStream sEntrada;
        ObjectOutputStream sSalida;

        // ID unico: facilita la desconexion
        int id;
        // el nombre de usuario del cliente
        String nombreUsuario;
        // identificador de comandos recibidos
        MensajeChat cm;
        MensajeChat2 aja;
        Object cm2;
        // Fecha
        String fecha;

        ClientThread (Socket socket) {
            id = ++idUnico;
            this.socket = socket;

            System.out.println("Hilo tratando de crear los objectos de Stream");
            try{
                sSalida = new ObjectOutputStream(socket.getOutputStream());
                sEntrada = new ObjectInputStream(socket.getInputStream());

                // leer nombre de usuario
                nombreUsuario = (String) sEntrada.readObject();
                System.out.println("**********************************");
                System.out.println(nombreUsuario);
                System.out.println("**********************************");
                controladorServidor.agregarUsuario(nombreUsuario);
                difundirMsg(nombreUsuario + ":QUIENESTA"); // difundir un mensaje de nuevo usuario conectado
                writeMsg(nombreUsuario + ":QUIENESTA");
                int variable = 1/0;
                for(ClientThread cliente : clientesConectados){
                    writeMsg(cliente.nombreUsuario + ":QUIENESTA");
                }
            }
            catch(IOException e){
                String msg = "Excepcion creando nuevo Stream de Entrada/Salida: " + e;
                mostrarLog("Excepcion creando nuevo Stream de Entrada/Salida: " + e);
                logger.log(Level.FATAL, msg);
            }
            catch (ClassNotFoundException e) {
            }
            fecha = new Date().toString() + "\n";
        }

        // funcion que correra continuamente
        public void run(){
            // correr continuamente hasta un LOGOUT
            boolean continuarCorriendo = true;
            while(continuarCorriendo) {
                // leer un String como objecto
                try {
                    cm2 = sEntrada.readObject();
                    //cm = (MensajeChat) cm2;
                    //entero = (int) sEntrada.readByte();
                    if(cm2 instanceof MensajeChat2)
                    {
                        System.out.println("Por fin Mensaje Chat 2");
                        aja = (MensajeChat2) cm2;
                        cm = new MensajeChat(aja.obtenerTipo(), Integer.toString(aja.obtenerMensaje()));
                        System.out.println(cm2.getClass().getSimpleName());

                        String str = (String) cm2.getClass().getSimpleName();
                        String str2 = "MensajeChat2";
                        SwitchClases.Clazz claseObjeto = SwitchClases.Clazz.valueOf(cm2.getClass().getSimpleName());
                        if(str2 == str){
                            System.out.println("Si es igual");
                        }
                    }
                    else if(cm2 instanceof MensajeChat){
                        System.out.println("Por fin Mensaje Chat 1");
                        cm = (MensajeChat) cm2;
                        System.out.println(cm.getClass().getSimpleName());
                    }

                }
                catch (IOException e) {
                    mostrarLog(nombreUsuario + " Exepcion leyendo el Stream: " + e);
                    break;
                }
                catch (ClassNotFoundException e2){
                    break;
                }
                String mensaje = cm.obtenerMensaje();

                // Verificar de acuerdo al tipo de mensaje recibido
                switch (cm.obtenerTipo()){
                    case MensajeChat.MENSAJE:
                        difundirMsg(nombreUsuario + ": " + mensaje);
                        break;
                    case MensajeChat.LOGOUT:
                        mostrarLog(nombreUsuario + " desconectar con un mensaje de LOGOUT.");
                        difundirMsg(nombreUsuario + ":REMOVE");
                        break;
                }
            }
            //eliminar de la lista que contiene los clientes conectados
            remove(id);
            close();
        }

        // Intentar cerrar todo
        private void close() {
            // intentar cerrar la conexion
            try {
                if(sSalida != null) sSalida.close();
            }
            catch(Exception e) {}
            try {
                if(sEntrada != null) sEntrada.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }

        /**
         * Escribir mensaje de salida al cliente
         */
        private boolean writeMsg(String msg) {
            // si el cliente aun esta conectado, enviar un mensaje
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sSalida.writeObject(msg);
            }
            // si ocurre un error, no abortar solo informar al usuario
            catch(IOException e) {
                mostrarLog("Error sending message to " + nombreUsuario);
                mostrarLog(e.toString());
            }
            return true;
        }
    }

}
