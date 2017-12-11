package kw46;

import java.io.IOException;

import kw45.TcpSocket;

public class MainKW46 {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("wrong number of Arguments");
			System.exit(0);
		}

		if (args[0].equals("-l")) {
			System.out.println("Starting tcp netcat in server mode...");
			int port = Integer.parseInt(args[1]);
			TcpSocket socket = new TcpSocket(port);
			Transceiver transceiver = new Transceiver(socket);
			transceiver.listen();
		}

		else {
			System.out.println("Starting tcp netcat in client mode...");
			int port = Integer.parseInt(args[1]);
			TcpSocket socket = new TcpSocket(args[0], port);
			Transceiver transceiver = new Transceiver(socket);
			transceiver.listen();
		}

	}
}
