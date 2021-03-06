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
		transmitter.tell("\u0004", null);
		System.out.println("EOT sent, shutting down Output...");
		scanner.close();
	}
	
	public void close() {
		
	}

}
