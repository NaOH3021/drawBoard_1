import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SimplePaintServer {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        JLabel label = new JLabel();
        frame.add(label);

        frame.setVisible(true);

        try {
            // Create DatagramSocket object and specify port number
            DatagramSocket socket = new DatagramSocket(8888);

            // Create byte array to store received data
            byte[] buffer = new byte[WIDTH * HEIGHT * 4]; // Assuming RGBA format for image data

            while (true) {
                // Create DatagramPacket object to store received data
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive data
                socket.receive(packet);

                // Convert received data to BufferedImage
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
                BufferedImage image = ImageIO.read(byteArrayInputStream);

                // Display the received image on the label
                label.setIcon(new ImageIcon(image));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
