package kw43;

public class Transmitter implements Actor {
	private final UdpSocket socket;

	public Transmitter(UdpSocket socket) {
		this.socket = socket;
	}

	@Override
	public void tell(String message, Actor sender) {
		socket.send(message);
	}

	@Override
	public void shutdown() {

	}

}
