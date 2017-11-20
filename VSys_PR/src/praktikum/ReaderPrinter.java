package praktikum;

public class ReaderPrinter implements Actor{
	private Reader reader;
	private Printer printer;
	
	public ReaderPrinter(Transceiver transceiver){
		printer = new Printer();
		reader = new Reader(transceiver);
	}

	public void read(){
		reader.read();
	}
	
	@Override
	public void tell(String message, Actor sender) {
		printer.tell(message, sender);
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	
}
