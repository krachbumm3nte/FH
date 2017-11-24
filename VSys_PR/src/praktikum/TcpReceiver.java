package praktikum;

import java.io.BufferedReader;
import java.io.IOException;

public class TcpReceiver implements Runnable {
	private TcpSocket socket;

	private Actor printer = new Printer();

	public TcpReceiver(TcpSocket socket) {
		this.socket = socket;
	}

	public void listen() throws IOException {
		new Thread(this).start();
	}

	@Override
	public void run() {
		BufferedReader in = socket.getIn();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				printer.tell(line, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
