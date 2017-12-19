package kw47;

import java.io.IOException;
import java.net.Inet4Address;

import kw45.TcpSocket;
import kw46.Transceiver;

public class IRCMain_1 {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("wrong number of Arguments");
			System.exit(0);
		}

		else if (args[0].equals("-l")) {
			int port = Integer.parseInt(args[1]);
			System.out.println("Starting IRC Server on " + Inet4Address.getLocalHost().getHostAddress() + ", port " + port + "...");
			IRCServer server = new IRCServer(port);
			server.run();
		}

		else {
			System.out.println("IRC chat client...");
			int port = Integer.parseInt(args[1]);
			TcpSocket socket = new TcpSocket(args[0], port);
			Transceiver transceiver = new Transceiver(socket);
			transceiver.listen();
		}
	}
}
