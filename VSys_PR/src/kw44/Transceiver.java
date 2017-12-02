package kw44;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import kw43.Actor;
import kw43.Receiver;
import kw43.Transmitter;
import kw43.UdpSocket;

public class Transceiver implements Actor {
	
	private Transmitter transmitter;
	private Receiver receiver;
	private ReaderPrinter readerPrinter;
	
	public Transceiver(int port) throws SocketException, UnknownHostException{
		UdpSocket socket = new UdpSocket(port);
		receiver = new Receiver(socket);
		DatagramPacket packet = receiver.receive();
		receiver.listen();
		readerPrinter = new ReaderPrinter(this);
		socket.connect(packet.getAddress(), packet.getPort());
		System.out.println("connected");
		transmitter = new Transmitter(socket);
		readerPrinter.read();

	}
	
	public Transceiver(String host, int port) throws SocketException, UnknownHostException {
		UdpSocket socket = new UdpSocket(host, port);
		receiver = new Receiver(socket);
		transmitter = new Transmitter(socket);
		readerPrinter= new ReaderPrinter(this);
		receiver.listen();
		readerPrinter.read();
	}
	
	@Override
	public void tell(String message, Actor sender) {
		transmitter.tell(message, sender);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
