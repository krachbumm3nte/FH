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
import java.util.stream.Collectors;

import kw43.Actor;

public class IRCServer implements Runnable, Actor {

	private Map<String, Client> clients;
	private Map<String, Channel> channels;
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
		channels = new HashMap<String, Channel>();
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

		case "NAMES":
			names(sender, m);
			break;

		default:
			sender.sendReply(421, m.getCommand());
			break;
		}
	}

	private void names(Client sender, Message m) {
		String channel;
		if ((channel = m.getArgs()[0]) != null) {
			sender.sendReply(353, listNames(channel));
		} else {
			sender.sendReply(353, "#foobar :foobar1 foobar2 foobar3");
		}

		sender.sendReply(366, channel);

	}

	private String listNames(String channel) {
		List<String> names = channels.get(channel).getClients();
		StringBuilder sb = new StringBuilder();
		sb.append(channel + ":");
		names.forEach(e -> sb.append("\n\t" + e));
		return sb.toString();
	}

	private void joinChannel(Client sender, Message m) {
		if (!m.enoughParams(1)) {
			sender.sendReply(461, m.getCommand());
		} else {
			String channel = m.getArgs()[0];
			if (channel.charAt(0) != '#') {
				sender.sendReply(476, channel);
			} else {
				if (!channels.containsKey(channel)) {
					channels.put(channel, new Channel("No topic is set"));
				}
				sender.join(channel);
				channels.get(channel).addClient(sender.getNick());
				sender.sendReply(332, String.format("%s :%s", channel, channels
						.get(channel).getTopic()));
				names(sender, m);
			}
		}
	}

	private void whoIs(Client sender, Message m) {
		if (m.enoughParams(1)) {
			String nick = m.getArgs()[0];
			if (!clients.containsKey(nick)) {
				sender.sendReply(401, nick);
			} else {
				sender.sendReply(311, nick);
				sender.sendReply(312, nick);
				sender.sendReply(318, nick);
			}
		} else {
			sender.sendReply(461, m.getCommand());
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
		if (m.enoughParams(2)) {
			if (c.getName() != null || c.getUser() != null) {
				c.sendReply(462, null);
			} else {
				c.setName(m.getArgs()[0]);
				c.setUser(m.getText());

				if (!c.getNick().contains(unknownUser) && !c.receivedWelcome()) {
					welcomeUser(c);
				}
			}
		} else {
			c.sendReply(461, m.getCommand());
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
		} else if (m.getText() == null) {
			sender.sendReply(412, m.getCommand());
		} else if (target.charAt(0) == '#') {
			List<String> users = channels.get(target).getClients();
			users.forEach(e -> clients.get(e).sendMessage(formatPrivateMessage(sender, m)));
		} else {
			if(!clients.containsKey(target)) {
				sender.sendReply(401, target);
			}else {
			clients.get(target).sendMessage(formatPrivateMessage(sender, m));
			}
		}
	}

	private String formatPrivateMessage(Client sender, Message m) {
		return String.format("%s PRIVMSG %s :%s", sender.getFull(),
				m.getArgs()[0], m.getText());
	}

	private void notice(Client sender, Message m) {
		String target = m.getArgs()[0];
		if (target.charAt(0) == '#') {
			List<String> users = channels.get(target).getClients();
			users.forEach(e -> clients.get(e).sendMessage(formatPrivateMessage(sender, m)));
		}
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
		// todo
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
