package kw45;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kw43.Actor;
import kw43.Printer;

public class Receiver implements Runnable {
	private TcpSocket socket;

	private Actor printer = new Printer();

	public Receiver(TcpSocket socket) {
		this.socket = socket;
	}

	public void listen() throws IOException {
		new Thread(this).start();
	}

	@Override
	public void run() {
		InputStream in = socket.getIn();
		byte[] line = new byte[1024];
		
		try {
			in.read(line);
			while (!new String(line).contains("\u0004")) {
				
				printer.tell(new String(line), null);
				in.read(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("received EOT - Shutting down...");
		System.exit(0);
	}

}
