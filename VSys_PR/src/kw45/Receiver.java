package kw45;

import java.io.BufferedReader;
import java.io.IOException;
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
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getIn()));
		String line;
		try {
			while ((line = in.readLine()) != null) {
				printer.tell(line, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("received EOT - Shutting down...");
		System.exit(0);
	}

}
