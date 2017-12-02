package kw43;

public interface Actor {

	void tell(String message, Actor sender);

	void shutdown();

}
