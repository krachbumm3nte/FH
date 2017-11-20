package praktikum;

import java.net.DatagramPacket;

public class Receiver implements Runnable {
	private final int LENGTH = 1024;
	private final UdpSocket socket;

	private Actor printer = new Printer();

	public Receiver(UdpSocket socket) {
		this.socket = socket;
	}

	public DatagramPacket receive() {
		return socket.receive(LENGTH);
	}

	public void listen() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		DatagramPacket packet;
		while ((packet = socket.receive(LENGTH)).getData().toString() != "\u0004") {
			printer.tell(new String(packet.getData(), packet.getOffset(), packet.getLength()), null);
		}
	}

}
