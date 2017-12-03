package kw43;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSocket{
	private int port;
	private InetAddress addr;
	private DatagramSocket socket;
	private final int BUFFER = 1024;

	public UdpSocket(int port) throws SocketException {
		this.port = port;
		this.socket = new DatagramSocket(port);
	}

	public UdpSocket(String host, int port) throws SocketException, UnknownHostException {
		this.port = port;
		addr = InetAddress.getByName(host);
		socket = new DatagramSocket();
		socket.connect(addr, port);
	}

	public boolean send(String message) {
		if (message == null)
			return false;

		byte[] buffer = message.getBytes();
		int BUFSIZE = buffer.length;
		try {
			socket.send(new DatagramPacket(buffer, BUFSIZE, addr, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public DatagramPacket receive(int bufferlength) {

		DatagramPacket data = new DatagramPacket(new byte[BUFFER], BUFFER);

		try {
			socket.receive(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public boolean connect(InetAddress addr, int port) {
		if (!addr.equals(this.addr) || port != this.port) {
			socket.connect(addr, port);
			this.port = port;
			this.addr = addr;
			System.out.println("connected to new client");
			return true;
		}
		return false;
	}
}
