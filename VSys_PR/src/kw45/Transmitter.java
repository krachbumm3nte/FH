package kw45;

import java.io.IOException;

import kw43.Actor;

public class Transmitter implements Actor {

	private TcpSocket socket;

	public Transmitter(TcpSocket socket) throws IOException {
		this.socket = socket;
	}

	@Override
	public void tell(String message, Actor sender) {
		socket.send(message);
	}

	@Override
	public void shutdown() {

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
