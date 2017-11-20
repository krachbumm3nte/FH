package praktikum;

import java.io.IOException;

public class TcpTransmitter implements Actor {

	private TcpSocket socket;

	public TcpTransmitter(TcpSocket socket) throws IOException {
		this.socket = socket;
	}

	@Override
	public void tell(String message, Actor sender) {
		socket.send(message);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
