package kw47;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import kw43.Actor;

public class IRCServer implements Runnable, Actor {

	private Map<String, Client> clients;

	private boolean running;
	private int port;
	private static int totalUsers;
	private String host, created;
	private String unknownUser = "unknownUser_";

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
		this.created = new SimpleDateFormat("dd. MM. yyyy, HH:mm:ss").format(new java.util.Date());
		while (running) {
			try {
				ServerSocket servS = new ServerSocket(port);
				Socket s = servS.accept();

				String name = unknownUser + ++totalUsers;
				System.out.println(
						"connected to a new User (" + totalUsers + " total User" + (totalUsers > 1 ? "s" : "") + ")");
				Client c = new Client(s, this, name);
				// c.sendMessage("You joined an IRC server on " + host + ", port " + port +
				// ".");
				// c.sendMessage("please log in using the NICK and USER commands before
				// proceeding.");
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

		case "QUIT":
			quitUser(sender, m);
			break;

		case "PRIVMSG":
			System.out.println("sending private message...");
			sendPrivateMessage(sender, m);
			break;

		case "NOTICE":
			notice(sender, m);
			break;

		default:
			sender.sendReply(421, m.getArgs()[0]);
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

			sendToAllOthers("changed NICK of " + old + " to " + nick, c);
			if (c.getName() != null && c.getUser() != null) {
				welcomeUser(c);
			}
		}
	}

	public void quitUser(Client c, Message m) {
		String quitmsg = m.getCmd() == null ? "Client quit" : m.getCmd();
		sendToAllOthers(String.format("%s QUIT :%s", c.getFull(), quitmsg), c);
		c.sendMessage(String.format("Closing Link: %s (%s)", c.getHost(), quitmsg));
		c.shutdown();
		clients.remove(c.getNick());
	}

	public void changeUser(Client c, Message m) {
		if (c.getName() != null || c.getUser() != null) {
			c.sendReply(462, null);
		} else {
			c.setName(m.getArgs()[1]);
			c.setUser(m.getCmd());

			if (!c.getNick().contains(unknownUser)) {
				welcomeUser(c);
			}
		}
	}

	private void welcomeUser(Client c) {
		c.sendReply(001, null);
		c.sendReply(002, null);
		c.sendReply(003, null);
		c.sendReply(004, null);
	}

	private void sendPrivateMessage(Client sender, Message m) {
		String target = m.getArgs()[1];
		
		if (!clients.containsKey(target)) {
			sender.sendReply(401, target);
		} else {
			System.out.println("sending to " + target);
			clients.get(target).sendMessage(String.format("%s PRIVMSG %s :%s", sender.getFull(), target, m.getCmd()));
		}
	}

	private void notice(Client sender, Message m) {
		String target = m.getArgs()[1];
		if (clients.containsKey(target)) {
			clients.get(target).sendMessage(String.format("%s NOTICE %s :%s", sender.getFull(), target, m.getCmd()));
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public String getVersion() {
		return version;
	}

	public String getHost() {
		return host;
	}

	public String getCreated() {
		return created;
	}

	public void sendToAll(String message) {
		clients.entrySet().stream().forEach(e -> e.getValue().sendMessage(message));
		System.out.println("sending to all users: " + message);
	}

	public void sendToAllOthers(String message, Client c) {
		clients.entrySet().stream().filter(e -> e.getKey() != c.getNick())
				.forEach(e -> e.getValue().sendMessage(message));
	}

}
