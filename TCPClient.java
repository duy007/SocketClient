import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(args[0]);
		System.out.println(args[1]);
	    try (Socket socket = new Socket(args[0], Integer.valueOf(args[1]))) {
	        socket.setSoTimeout(15000);
	        OutputStream out = socket.getOutputStream();
	        if (args.length > 2) {
	        	System.out.println("Writing initial input");
	        	String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
	        	out.write((msg + "\n").getBytes());
	        } else {
	        	 String get = "GET / HTTP/1.0\n\n";
	             out.write(get.getBytes());
	        }
	        Thread thread = new Thread(() -> {
	        	System.out.println("Starting Thread for user Input");
	        	System.out.println("'exit' to close connection");
	        	System.out.println("Input:");
	        	try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
	        		String so;
		        	while(((so = br.readLine()) != null)) {
		        		if (!socket.isClosed()) {
			        		out.write((so + "\n").getBytes());
			        		if (so.equalsIgnoreCase("exit")) {
			        			out.flush();
			        			break;
			        		}
		        		} else {
		        			System.out.println("Socket Close, Threading Closing");
		        			break;
		        		}
		        			
		        	}
	        	} catch (IOException ex) { System.err.println(ex); }
	        });
	        thread.start();
	        InputStream inputStream = socket.getInputStream();
	        int readChar = inputStream.read();
            while (readChar != -1 && thread.isAlive() && !socket.isClosed()) {
                System.out.write(readChar);
                readChar = inputStream.read();
            }
            if (!thread.isAlive() && !socket.isClosed()) {
            	System.out.println("Closing Socket");
            	socket.close();
            }
	    } catch (IOException ex) { System.err.println(ex); }
	}

}
