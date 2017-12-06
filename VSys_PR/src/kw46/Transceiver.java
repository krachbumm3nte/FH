package kw46;

import java.io.IOException;
import java.io.InputStream;
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

			InputStream in = socket.getIn();
			byte[] line = new byte[1024];
			in.read(line);
			while (new String(line) != "\u0004") {
				readerprinter.tell(new String(line), null);
				in.read(line);
			}
			System.out.println("Received EOT - Shutting down Input...");
			in.close();

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
