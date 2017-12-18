package kw46;

import kw43.Actor;
import kw43.Reader;

public class ReaderPrinter implements Actor{
	private Actor transmitter;
	private Reader reader;
	
	public ReaderPrinter(Actor transceiver){
		this.transmitter = transceiver;
		reader = new Reader(transmitter);
		read();
	}

	public void read(){
		reader.read();
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
