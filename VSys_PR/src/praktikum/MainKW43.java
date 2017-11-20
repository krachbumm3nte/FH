package praktikum;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MainKW43 {
	public static void main(String[] args) throws SocketException, UnknownHostException {
		if (args.length != 2) {
			System.err.println("wrong number of Arguments");
			System.exit(0);
		}

		if (args[0].equals("-l")) {
			System.out.println("Starting netcat in server mode...");
			int port = Integer.parseInt(args[1]);
			Receiver receiver = new Receiver(new UdpSocket(port));
			receiver.listen();
		} else {
			System.out.println("Starting netcat in client mode...");
			int port = Integer.parseInt(args[1]);
			Transmitter transmitter = new Transmitter(new UdpSocket(args[0], port));
			Reader reader = new Reader(transmitter);
			reader.run();
		}

	}
}
