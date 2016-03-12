import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.io.*;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Input parameters: <IP> <port> <file>");
			System.exit(-1);
		}

		Socket socket = null;
		try {
			// create the socket by invoking Socket constructor
			socket = new Socket(args[0], Integer.parseInt(args[1]));

			File file = new File(args[2]);
	        
	        byte[] bytes = new byte[16 * 1024];
	        InputStream in = new FileInputStream(file);
	        OutputStream out = socket.getOutputStream();

			byte sendline[] = args[2].getBytes();
			out.write(sendline, 0, args[2].length());

	        int count;
	        while ((count = in.read(bytes)) > 0) {
	            out.write(bytes, 0, count);
	        }

			out.close();
        	in.close();
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