package kw44;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MainKW44 {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws SocketException, UnknownHostException {
		if (args.length != 2) {
			System.err.println("wrong number of Arguments");
			System.exit(0);
		}

		if (args[0].equals("-l")) {
			System.out.println("Starting netcat in server mode...");
			int port = Integer.parseInt(args[1]);
			Transceiver transceiver = new Transceiver(port);
		} else {
			System.out.println("Starting netcat in client mode...");
			int port = Integer.parseInt(args[1]);
			Transceiver transceiver = new Transceiver(args[0], port);
			
		}

	}
}
