package kw46;

import java.util.Scanner;

import kw43.Actor;

public class ReaderPrinter implements Actor, Runnable{
	private Actor actor;
	
	public ReaderPrinter(Actor transceiver){
		this.actor = transceiver;
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
			actor.tell(line, null);
		}
		actor.tell("\u0004", null);
		scanner.close();
		System.out.println("EOT sent - Shutting down Output...");
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
