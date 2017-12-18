package kw45;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kw43.Actor;

public class Receiver implements Runnable {
	private TcpSocket socket;
	private Actor printer;

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
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getIn()));
			String line;
			while(!(line = in.readLine()).contains("\u0004")){
				printer.tell(line, null);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("received EOT, shutting down Input...");
	}

}
