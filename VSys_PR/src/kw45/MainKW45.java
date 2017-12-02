package kw45;

import java.io.IOException;

import kw43.Reader;

public class MainKW45 {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("wrong number of Arguments");
			System.exit(0);
		}

		if (args[0].equals("-l")) {
			System.out.println("Starting tcp netcat in server mode...");
			int port = Integer.parseInt(args[1]);
			TcpSocket socket = new TcpSocket(port);
			Receiver receiver = new Receiver(socket);
			receiver.listen();
			

		} else {
			System.out.println("Starting netcat in client mode...");
			int port = Integer.parseInt(args[1]);
			Transmitter transmitter = new Transmitter(new TcpSocket(args[0], port));
			Reader reader = new Reader(transmitter);
			reader.read();

		}

	}
}
