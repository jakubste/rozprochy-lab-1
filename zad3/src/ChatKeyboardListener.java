import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.logging.Logger;


public class ChatKeyboardListener implements Runnable {
    private static String PROPERTIES_FILENAME = "app.properties";

    private static int TTL = 2;
    private static int PORT = 5000;
    private static String GROUP = "225.0.0.37";

    private static int BUFFER_LENGTH = 1024;
    private byte buffer[] = new byte[BUFFER_LENGTH];
    private String username;
    private Logger logger = Logger.getLogger(ChatKeyboardListener.class.getName());

    ChatKeyboardListener(String username) {
        this.username = username;
    }

    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    socket.close();
                }
            });
            InetAddress address = InetAddress.getByName(GROUP);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);

            Scanner scanner = new Scanner(System.in);
            String line;
            while (true) {
                line = scanner.nextLine();

                ChatData data = new ChatData(username, line);
                packet.setData(data.getBytes());
                socket.setTimeToLive(TTL);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
