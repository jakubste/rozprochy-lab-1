import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.math.BigInteger;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Input parameters: <IP> <port> <pi_digit_number>");
			System.exit(-1);
		}

		Socket socket = null;
		try {
			// create the socket by invoking Socket constructor
			socket = new Socket(args[0], Integer.parseInt(args[1]));

			InputStream in = socket.getInputStream(); // - get input stream from socket
			OutputStream out = socket.getOutputStream(); // - get output stream from socket

			BigInteger num = new BigInteger(args[2]);

			byte bb[] = num.toByteArray();

			byte sendline[] = bb;
			byte recvline[] = new byte[80]; // - just create byte buffer with some arbitrary length

			// send data by invoking write method on output stream passing sendline byte buffer
			out.write(sendline);

			// print the length of sent data (length of sendline buffer)
			System.out.println("sent bytes: " + sendline.length);

			// print sent data by passing sendline buffer to String constructor
			// System.out.println("sent: " + new String(num));

			// receive data by invoking read method on output stream
			int len = in.read(recvline);

			// print the length of received data (returned from read method)
			System.out.println("received bytes: " + len);

			// print received data by passing recvline buffer to String contructor with index and offset
			System.out.println("received: " + recvline[0]);

			socket.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}