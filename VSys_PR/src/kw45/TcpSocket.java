package kw45;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpSocket {
	private Socket socket;
	private InputStream in;

	public TcpSocket(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		in = socket.getInputStream();
	}

	public TcpSocket(int port) throws IOException {
		ServerSocket servS = new ServerSocket(port);
		socket = servS.accept();
		in = socket.getInputStream();
		servS.close();
	}

	public TcpSocket(Socket s) throws IOException {
		socket = s;
		in = socket.getInputStream();

	}

	public void send(String message) {
		try {
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

	public void close() throws IOException {
		socket.close();
	}

	public String getHost() {
		return socket.getInetAddress().toString();
	}
}
