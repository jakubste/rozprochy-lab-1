import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient {
    private static int PORT = 5000;
    private static String GROUP = "225.0.0.37";
    private static int BUFFER_LENGTH = 1024;
    private static int MAX_USERNAME_LENGTH = 6;
    private static byte buffer[] = new byte[BUFFER_LENGTH];

    private static Logger logger = Logger.getLogger(ChatClient.class.getName());
    private static String nickname;
    private static ChatKeyboardListener keyboardListener;
    private static Thread keyboardListenerThread;


    public static void runKeyboardListener() {
        keyboardListener = new ChatKeyboardListener(nickname);
        keyboardListenerThread = new Thread(keyboardListener);
        keyboardListenerThread.start();
    }

    public static void main(String[] args) {
        logger.setLevel(Level.ALL);

        if (args.length != 1) {
            System.out.println("Input parameters: <nickname>");
            return;
        }

        if (args[0].length() > MAX_USERNAME_LENGTH) {
            System.out.println("Nickname length must be 6 characters or less.");
            return;
        }

        nickname = args[0];

        runKeyboardListener();

        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(InetAddress.getByName(GROUP));

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        socket.leaveGroup(InetAddress.getByName(GROUP));
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            DatagramPacket packet = new DatagramPacket(buffer, BUFFER_LENGTH);
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("dd.MM.yy hh:mm:ss");

            while (true) {
                socket.receive(packet);
                ChatData chatPacket = new ChatData(packet.getData());
                if (chatPacket.is_valid()) {
                    if (!chatPacket.getUsername().equals(nickname)) {

                        System.out.printf(
                            "[%s | %s] %s\n",
                            chatPacket.getUsername(),
                            formatter.format(new Date(chatPacket.getTime())),
                            chatPacket.getMessage()
                        );
                    }
                } else {
                    System.out.println("Coś się zepsuło i nie było mnie słychać.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
