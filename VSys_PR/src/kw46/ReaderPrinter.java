package kw46;

import java.util.Scanner;

import kw43.Actor;

public class ReaderPrinter implements Actor, Runnable{
	private Actor transmitter;
	
	public ReaderPrinter(Actor transceiver){
		this.transmitter = transceiver;
	}

	public void read(){
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
//		transmitter.tell("\u0004", null);
		transmitter.shutdown();
		scanner.close();
	}
	
	@Override
	public void tell(String message, Actor sender) {
		System.out.println("received: " + message);
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	
}
