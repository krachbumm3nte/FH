package kw47;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kw43.Actor;

public class IRCServer implements Runnable, Actor {

	private Map<String, Client> clients;
	private Map<String, String> channels;
	private boolean running;
	private int port, totalUsers;
	private String host, created, motd;
	private String unknownUser = "unknownUser_";

	private String version = "1.0";
	


	public IRCServer(int port) throws IOException {
		clients = new HashMap<String, Client>();
		this.port = port;
		running = true;
		host = Inet4Address.getLocalHost().getHostName();
		motd = "Willkommen im Praktikum";
		channels = new HashMap<String, String>();
	}

	@Override
	public void run() {
		System.out.println("Server running, waiting for connections...");
		created = new SimpleDateFormat("dd. MM. yyyy, HH:mm:ss")
				.format(new java.util.Date());
		while (running) {
			try {
				ServerSocket servS = new ServerSocket(port);
				Socket s = servS.accept();

				String name = unknownUser + ++totalUsers;
				System.out.println("connected to a new User (" + totalUsers
						+ " total User" + (totalUsers > 1 ? "s" : "") + ")");
				Client c = new Client(s, this, name);
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
		System.out.println("received message : " + message);
		Message m = new Message(message);

		switch (m.getCommand()) {
		case "NICK":
			changeNick(sender, m.getArgs()[0]);
			break;

		case "USER":
			changeUser(sender, m);
			break;

		case "QUIT":
			quitUser(sender, m);
			break;

		case "PRIVMSG":
			sendPrivateMessage(sender, m);
			break;

		case "NOTICE":
			notice(sender, m);
			break;

		case "PING":
			ping(sender, m);
			break;

		case "PONG":
			break;

		case "MOTD":
			messageOfTheDay(sender);
			break;

		case "LUSERS":
			lusers(sender);
			break;

		case "WHOIS":
			whoIs(sender, m);
			break;
			
		case "JOIN":
			joinChannel(sender, m);
			break;

		default:
			sender.sendReply(421, m.getCommand());
			break;
		}
	}

	private void joinChannel(Client sender, Message m) {
		String channel = m.getArgs()[0];
		if(!channels.containsKey(channel)) {
			channels.put(channel, "No topic is set");
		}
		sender.join(channel);
		
		if(channels.get(channel) != null) sender.sendReply(332, String.format("%s :%s", channel, channels.get(channel)));
	}
	
	


	private void whoIs(Client sender, Message m) {
		if (m.enoughParams(sender, 1)) {
			String nick = m.getArgs()[0];
			if (!clients.containsKey(nick)) {
				sender.sendReply(401, nick);
			} else {
				sender.sendReply(311, nick);
				sender.sendReply(312, nick);
				sender.sendReply(318, nick);
			}
		}
	}

	private void messageOfTheDay(Client sender) {
		sender.sendReply(375, host);
		sender.sendReply(372, motd);
		sender.sendReply(376, null);
	}

	private void ping(Client sender, Message m) {
		sender.sendMessage("PONG " + host);
	}

	public synchronized void changeNick(Client c, String nick) {
		if (clients.containsKey(nick)) {
			c.sendReply(433, nick);
		} else if (nick == null) {
			c.sendReply(431, nick);
		} else {

			String old = c.getNick();
			c.setNick(nick);

			clients.put(nick, c);
			clients.remove(old);

			sendToAllOthers("changed NICK of " + old + " to " + nick, c);
			if (c.getName() != null && c.getUser() != null
					&& !c.receivedWelcome()) {
				welcomeUser(c);
			}
		}
	}

	public void quitUser(Client c, Message m) {
		String quitmsg = m.getText() == null ? "Client quit" : m.getText();
		sendToAllOthers(String.format("%s QUIT :%s", c.getFull(), quitmsg), c);
		c.sendMessage(String.format("Closing Link: %s (%s)", c.getHost(),
				quitmsg));
		c.shutdown();
		clients.remove(c.getNick());
	}

	public void changeUser(Client c, Message m) {
		if (m.enoughParams(c, 2) == true) {
			if (c.getName() != null || c.getUser() != null) {
				c.sendReply(462, null);
			} else {
				c.setName(m.getArgs()[0]);
				c.setUser(m.getText());

				if (!c.getNick().contains(unknownUser) && !c.receivedWelcome()) {
					welcomeUser(c);
				}
			}
		}
	}

	private void welcomeUser(Client c) {
		c.sendReply(001, null);
		c.sendReply(002, null);
		c.sendReply(003, null);
		c.sendReply(004, null);
		c.welcome();
	}

	private void sendPrivateMessage(Client sender, Message m) {
		String target = m.getArgs()[0];

		if (target == null) {
			sender.sendReply(411, null);
		} else if (!clients.containsKey(target)) {
			sender.sendReply(401, target);
		} else if (m.getText() == null) {
			sender.sendReply(412, m.getCommand());
		} else {
			System.out.println("sending to " + target);
			clients.get(target).sendMessage(
					String.format("%s PRIVMSG %s :%s", sender.getFull(),
							target, m.getText()));
		}
	}

	private void notice(Client sender, Message m) {
		String target = m.getArgs()[0];
		if (clients.containsKey(target)) {
			clients.get(target).sendMessage(
					String.format("%s NOTICE %s :%s", sender.getFull(), target,
							m.getText()));
		}
	}

	private void lusers(Client c) {
		c.sendReply(251, null);
		c.sendReply(252, null);
		c.sendReply(253, null);
		c.sendReply(254, null);
		c.sendReply(255, null);
	}

	@Override
	public void shutdown() {
		//todo
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
		clients.entrySet().stream()
				.forEach(e -> e.getValue().sendMessage(message));
		System.out.println("sending to all users: " + message);
	}

	public void sendToAllOthers(String message, Client c) {
		clients.entrySet().stream().filter(e -> e.getKey() != c.getNick())
				.forEach(e -> e.getValue().sendMessage(message));
	}

	public int getClientCount() {
		return clients.size();
	}

	public int getUserCount() {
		return (int) clients.entrySet().stream()
				.filter(e -> e.getValue().receivedWelcome()).count();
	}

	public int getServiceCount() {
		return 0;
		// TODO
	}

	public int getServerCount() {
		return 1;
		// TODO
	}

	public int getOperatorCount() {
		return (int) clients.entrySet().stream()
				.filter(e -> e.getValue().isOP()).count();
	}

	public int getUnknownConnections() {
		return (int) clients.entrySet().stream()
				.filter(e -> !e.getValue().receivedWelcome()).count();
	}

	public int getChannelCount() {
		return channels.size();
	}

	public Client getClient(String nick) {
		return clients.get(nick);
	}

	public String getInfo() {
		return "FOO BAR";
	}
}
