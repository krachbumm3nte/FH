package kw47;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import kw43.Actor;

public class IRCServer implements Runnable, Actor {

	private Map<String, Client> clients;

	private boolean running;
	private int port;
	private static int totalUsers;
	private String host;
	private static String unknownUser = "unknownUser_";

	private String version = "1.0";

	public IRCServer(int port) throws IOException {
		clients = new HashMap<String, Client>();
		this.port = port;
		running = true;
		host = Inet4Address.getLocalHost().getHostName();
	}

	@Override
	public void run() {
		System.out.println("Server running, waiting for connections...");
		while (running) {
			try {
				ServerSocket servS = new ServerSocket(port);
				Socket s = servS.accept();

				String name = unknownUser + ++totalUsers;
				System.out.println(
						"connected to a new User (" + totalUsers + " total User" + (totalUsers > 1 ? "s" : "") + ")");
				Client c = new Client(s, this, name);
				c.sendMessage("You joined an IRC server on " + host + ", port " + port + ".");
				c.sendMessage("please log in using the NICK and USER commands before proceeding.");
				servS.close();
				clients.put(name, c);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void tell(String message, Actor sender) {

	}

	public void sendMessage(String message, Client sender) {
		Message m = new Message(message);
		switch (m.getArgs()[0]) {
		case "NICK":
			changeNick(sender, m.getArgs()[1]);
			break;

		case "USER":
			changeUser(sender, m);
			break;
		default:
			sender.sendMessage("Unknown command.");
			break;
		}
	}

	public synchronized void changeNick(Client c, String nick) {
		if (clients.containsKey(nick)) {
			c.sendReply(433, nick);
		} else {

			String old = c.getNick();
			c.setNick(nick);

			clients.put(nick, c);
			clients.remove(old);

			sendToAll("changed NICK of " + old + " to " + nick);
			if (c.getName() != null && c.getUser() != null) {
				c.sendReply(001, null);
			}
		}
	}

	public void changeUser(Client c, Message m) {

		c.setName(m.getArgs()[1]);
		c.setUser(m.getCmd());

		if (!c.getNick().contains(unknownUser)) {
			c.sendReply(001, null);
		}

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public String getHost() {
		return host;
	}

	public void sendToAll(String message) {
		clients.entrySet().stream().forEach(e -> e.getValue().sendMessage(message));
		System.out.println("sending to all users: " + message);
	}
	
	public void sendToAllOthers(String message, Client c) {
		clients.entrySet().stream().filter(e -> e.getKey()!= c.getNick()).forEach(e -> e.getValue().sendMessage(message));
	}

}
