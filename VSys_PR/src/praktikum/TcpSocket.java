package praktikum;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpSocket {
	private Socket socket;
	private DataOutputStream out;
	private BufferedReader in;


	public TcpSocket(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);		
		out = new DataOutputStream(socket.getOutputStream());
	}
	
	public TcpSocket(int port) throws IOException {
		ServerSocket servS = new ServerSocket(port);
		socket = servS.accept();
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		servS.close();
	}

	public TcpSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void send(String message){
		try {
			out.writeBytes(message + "\n");
			if(message.equals("\u0004")){
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedReader getIn() {
		return in;
	}

}
