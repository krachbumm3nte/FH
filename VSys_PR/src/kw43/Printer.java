package kw43;

public class Printer implements Actor {

	@Override
	public void tell(String message, Actor sender) {
		System.out.println("received: " + message);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
