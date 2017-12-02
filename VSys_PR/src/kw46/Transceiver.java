package kw46;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kw43.Actor;
import kw45.TcpSocket;

public class Transceiver implements Actor, Runnable {

	private TcpSocket socket;
	private ReaderPrinter readerprinter;

	public Transceiver(TcpSocket socket) {
		this.socket = socket;
		readerprinter = new ReaderPrinter(this);
		readerprinter.read();
	}

	public void listen() throws IOException {
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getIn()));
			String line;

			while ((line = in.readLine()) != null) {
				readerprinter.tell(line, null);
			}
			System.out.println("Verbindung beendet");
			readerprinter.shutdown();
			socket.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void tell(String message, Actor sender) {
		socket.send(message);
	}

	@Override
	public void shutdown() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
