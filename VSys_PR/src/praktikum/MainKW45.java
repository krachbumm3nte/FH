package praktikum;

import java.io.IOException;

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
			TcpReceiver receiver = new TcpReceiver(socket);
			receiver.listen();
			

		} else {
			System.out.println("Starting netcat in client mode...");
			int port = Integer.parseInt(args[1]);
			TcpTransmitter transmitter = new TcpTransmitter(new TcpSocket(args[0], port));
			Reader reader = new Reader(transmitter);
			reader.read();

		}

	}
}
