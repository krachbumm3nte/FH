package praktikum;

import java.io.BufferedReader;
import java.io.IOException;

public class TcpReceiver {
	private TcpSocket socket;

	private Actor printer = new Printer();

	public TcpReceiver(TcpSocket socket) {
		this.socket = socket;
	}

	public void listen() throws IOException {
		BufferedReader in = socket.getIn();
		String line;
		while ((line = in.readLine()) != null) {
			printer.tell(line, null);
		}
		System.exit(0);
	}

}
