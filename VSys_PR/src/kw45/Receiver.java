package kw45;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kw43.Actor;

public class Receiver implements Runnable {
	private TcpSocket socket;
	private Actor printer;
	private BufferedReader in;

	public Receiver(TcpSocket socket, Actor printer) {
		this.socket = socket;
		this.printer = printer;
	}

	public void listen() throws IOException {
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getIn()));
			String line;
			while((line = in.readLine()) != null){
				printer.tell(line, null);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
