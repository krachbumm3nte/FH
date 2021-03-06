package kw47;

import java.util.LinkedList;
import java.util.List;

public class Channel {
	private List<String> clients;
	private String topic;

	public Channel() {
		clients = new LinkedList<String>();
		topic = null;
	}

	public Channel(String s) {
		clients = new LinkedList<String>();
		topic = s;
	}

	public void setTopic(String s) {
		topic = s;
	}

	public String getTopic() {
		return topic;
	}

	public List<String> getClients() {
		return clients;
	}

	public void addClient(String s) {
		clients.add(s);
	}

	public void removeClient(String s) {
		clients.remove(s);
	}

	public boolean isEmpty() {
		return clients.size() == 0;
	}

}
