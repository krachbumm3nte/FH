package kw44;

import kw43.Actor;
import kw43.Reader;

public class ReaderPrinter implements Actor{
	private Reader reader;
	
	public ReaderPrinter(Transceiver transceiver){
		reader = new Reader(transceiver);
	}

	public void read(){
		reader.read();
	}
	
	@Override
	public void tell(String message, Actor sender) {
		System.out.println(message);
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	
}
