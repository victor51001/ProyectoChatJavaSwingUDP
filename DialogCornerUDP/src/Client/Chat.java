package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Chat {
    private String nick;
    private DatagramSocket socket;
    private Server servidor;
    HiloMensajes hiloM;
    private JPanel pnlChat;
    private JTextArea txtaText;
    private JTextArea txtaUsuers;
    private JButton bttnEnviar;
    private JTextField txtfMessage;
    private JButton bttnSalir;

    public Chat(String nick, DatagramSocket socket, Server serv) {
        this.nick = nick;
        this.socket = socket;
        this.servidor = serv;
        iniciarHiloMensajes();
        bttnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje(txtfMessage.getText());
                txtfMessage.setText("");
            }
        });
        bttnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    byte[] buffer = ("Salir:" + nick).getBytes();
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidor.getIp(), servidor.getPuerto());
                    socket.send(paquete);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                hiloM.interrupt();
                System.exit(0);
            }
        });
    }

    public void añadirMensaje(String mensaje) {
        txtaText.append(mensaje + "\n");
    }

    private void iniciarHiloMensajes() {
        HiloMensajes hiloMensajes = new HiloMensajes(socket, this);
        hiloM = hiloMensajes;
        hiloM.start();
    }

    public void enviarMensaje(String mensaje) {
        añadirMensaje(nick + ": " + mensaje);
        try {
            byte[] buffer = ("Mensaje:" + nick + ": "+mensaje).getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidor.getIp(), servidor.getPuerto());
            socket.send(paquete);
        } catch (IOException e) {
            throw new RuntimeException("Error al enviar el mensaje", e);
        }
    }

    public Container getPnlChat() {
        return pnlChat;
    }

    public void actualizarUsuarios(String mensaje) {
        txtaUsuers.setText(mensaje);
    }
}
