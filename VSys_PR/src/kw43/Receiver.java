package kw43;

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
		String message;
		while (true) {
			packet = socket.receive(LENGTH);
			message = new String(packet.getData(), packet.getOffset(), packet.getLength());
			printer.tell(message , null);
			socket.connect(packet.getAddress(), packet.getPort());
			if(message.equals("\u0004")) break;
		
		}
		System.out.println("received EOT, shutting down Receiver...");
		
	}

}
