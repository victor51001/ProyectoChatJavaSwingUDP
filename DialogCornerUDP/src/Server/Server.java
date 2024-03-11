package Server;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PUERTO = 12345;
    private static HashSet<Integer> clientes = new HashSet<>();
    private static HashSet<String> usuarios = new HashSet<>();

    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(PUERTO);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paqueteE = new DatagramPacket(buffer, buffer.length);
                server.receive(paqueteE);
                String mensaje = new String(paqueteE.getData()).trim();
                switch (mensaje.split(":")[0]) {
                    case "Nick":
                        boolean libre = usuarios.add(mensaje.split(":")[1]);
                        byte[] bufferS = String.valueOf(libre).getBytes();
                        server.send(new DatagramPacket(bufferS, bufferS.length, paqueteE.getAddress(), paqueteE.getPort()));
                        if (libre) {
                            clientes.add(paqueteE.getPort());
                            difundirUsuariosConectados(server);
                        }
                        break;
                    case "Salir":
                        clientes.remove(paqueteE.getPort());
                        usuarios.remove(mensaje.split(":")[1]);
                        difundirUsuariosConectados(server);
                        break;
                    case "Mensaje":
                        String mensaje2 = mensaje.split(":")[1].concat(": "+ mensaje.split(":")[2]);
                        for(int puerto : clientes) {
                            InetAddress ip = InetAddress.getByName("localhost");
                            if (puerto != paqueteE.getPort()) {
                                server.send(new DatagramPacket(mensaje2.getBytes(), mensaje2.getBytes().length, ip, puerto));
                            }
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void difundirUsuariosConectados(DatagramSocket server) throws UnknownHostException {
        StringBuilder listaUsuarios = new StringBuilder("Usuarios conectados:");
        for (String usuario : usuarios) {
            listaUsuarios.append(usuario).append(", ");
        }
        listaUsuarios.setLength(listaUsuarios.length() - 2);
        String usuariosConectados = listaUsuarios.toString();
        byte[] buffer = usuariosConectados.getBytes();
        for (Integer puerto : clientes) {
            InetAddress ip = InetAddress.getByName("localhost");
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, ip, puerto);
            try {
                server.send(paquete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}