package kw46;

import java.io.IOException;
import kw43.Actor;
import kw45.Receiver;
import kw45.TcpSocket;

public class Transceiver implements Actor {

	private TcpSocket socket;
	private Actor readerprinter;
	private Receiver receiver;
	private boolean connected;

	public Transceiver(TcpSocket socket) {
		this.socket = socket;
		readerprinter = new ReaderPrinter(this);
		receiver = new Receiver(socket, readerprinter);
		connected = false;
	}

	public Transceiver(TcpSocket socket, Actor readerprinter) {
		this.socket = socket;
		this.readerprinter = readerprinter;
		receiver = new Receiver(socket, readerprinter);
		connected = false;
	}

	public void listen() throws IOException {
		receiver.listen();
	}

	@Override
	public void tell(String message, Actor sender) {
		socket.send(message);
	}

	@Override
	public void shutdown() {

	}

	public boolean isConnected() {
		return connected;
	}

	public String getHost() {
		return socket.getHost();
	}
}
