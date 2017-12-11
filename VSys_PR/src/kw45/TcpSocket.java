package kw45;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpSocket {
	private Socket socket;
	private InputStream in;
	private OutputStream out;


	public TcpSocket(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);		
		in = socket.getInputStream();
		out = socket.getOutputStream();
		}
	
	public TcpSocket(int port) throws IOException {
		ServerSocket servS = new ServerSocket(port);
		socket = servS.accept();
		System.out.println("connected.");
		in = socket.getInputStream();
		out = socket.getOutputStream();
		servS.close();
	}


	
	public void send(String message){
		try {
//			out.write(message.getBytes());
//			out.flush();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getIn() {
		return in;
	}
	
	public void close() throws IOException{
		socket.close();
	}
}
