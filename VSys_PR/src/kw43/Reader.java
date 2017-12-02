package kw43;

import java.util.Scanner;

public class Reader implements Runnable {
	private final Actor transmitter;

	public Reader(Actor transmitter) {
		this.transmitter = transmitter;
	}

	public void read() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		String line;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			transmitter.tell(line, null);
		}
		transmitter.shutdown();
		scanner.close();
	}

}
