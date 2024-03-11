package Client;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Inicio extends JFrame{
    private JTextField txtfNick;
    private JPanel pnlCampo;
    private JButton bttnEntrar;
    private JPanel mainPanel;

    public static void main(String[] args) {
        Inicio ventana = new Inicio();
        ventana.setSize(400,300);
        ventana.setTitle("Ventana de inicio");
        ventana.setResizable(false);
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Inicio() {
        setContentPane(mainPanel);
        bttnEntrar.addActionListener(e -> {
            DatagramSocket socket = null;
            try {
                InetAddress ip = InetAddress.getByName("localhost");
                int puerto = 12345;
                Server servidor = new Server(puerto, ip);
                socket = new DatagramSocket();

                String nickname = txtfNick.getText();
                if (nickname.isEmpty()) {
                    mostrarError("El campo de nickname no puede estar vacío");
                } else {
                    byte[] bufferS = ("Nick:"+nickname).getBytes();
                    DatagramPacket paqueteS = new DatagramPacket(bufferS, bufferS.length, ip, puerto);
                    socket.send(paqueteS);

                    byte[] bufferE = new byte[1024];
                    DatagramPacket paqueteE = new DatagramPacket(bufferE, bufferE.length);
                    socket.receive(paqueteE);
                    String mensaje = new String(paqueteE.getData()).trim();
                    boolean respuesta = Boolean.parseBoolean(mensaje);
                    if (!respuesta) {
                        mostrarError("El nickname ya está en uso");
                    } else {
                        iniciarChat(nickname, socket, servidor);
                    }
                }

                } catch (IOException ex) {
                mostrarError("Error al conectarse al servidor: " + ex.getMessage());
                }
        });
    }
    private void iniciarChat(String nickname, DatagramSocket socket, Server servidor) {
        JFrame frame = new JFrame("Chat - " + nickname);
        frame.setContentPane(new Chat(nickname, socket, servidor).getPnlChat());
        frame.setSize(400, 300);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        dispose();
    }
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}