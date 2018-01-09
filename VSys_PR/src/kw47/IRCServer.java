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
	private final String UNKNOWNUSER = "unknownUser_";
	private String motd;

	private String version = "1.0";

	public IRCServer(int port) throws IOException {
		clients = new HashMap<String, Client>();
		this.port = port;
		running = true;
		host = Inet4Address.getLocalHost().getHostName();
		motd = "Willkommen im Praktikum";
	}

	@Override
	public void run() {
		System.out.println("Server running, waiting for connections...");
		this.created = new SimpleDateFormat("dd. MM. yyyy, HH:mm:ss").format(new java.util.Date());
		while (running) {
			try {
				ServerSocket servS = new ServerSocket(port);
				Socket s = servS.accept();

				String name = UNKNOWNUSER + ++totalUsers;
				System.out.println(
						"connected to a new User (" + totalUsers + " total User" + (totalUsers > 1 ? "s" : "") + ")");
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
		Message m = new Message(message);
		// if (!sender.receivedWelcome() && (m.getArgs()[0] != "NICK" || m.getArgs()[0]
		// != "USER")) {
		// sender.sendReply(451, null);
		// } else {
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

		default:
			sender.sendReply(421, m.getArgs()[0]);
			break;
		}
	}
	// }

	private void whoIs(Client sender, Message m) {
		if (m.enoughParams(sender, 1)) {
			String nick = m.getArgs()[1];
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
			if (c.getName() != null && c.getUser() != null && !c.receivedWelcome()) {
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
		if (m.enoughParams(c, 2) == true) {
			if (c.getName() != null || c.getUser() != null) {
				c.sendReply(462, null);
			} else {
				c.setName(m.getArgs()[1]);
				c.setUser(m.getCmd());

				if (!c.getNick().contains(UNKNOWNUSER) && !c.receivedWelcome()) {
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
		String target = m.getArgs()[1];

		if (target == null) {
			sender.sendReply(411, null);
		} else if (!clients.containsKey(target)) {
			sender.sendReply(401, target);
		} else if (m.getCmd() == null) {
			sender.sendReply(412, m.getArgs()[0]);
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

	private void lusers(Client c) {
		c.sendReply(251, null);
		c.sendReply(252, null);
		c.sendReply(253, null);
		c.sendReply(254, null);
		c.sendReply(255, null);
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

	public int getClientCount() {
		return clients.size();
	}

	public int getUserCount() {
		return (int) clients.entrySet().stream().filter(e -> e.getValue().receivedWelcome()).count();
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
		return (int) clients.entrySet().stream().filter(e -> e.getValue().isOP()).count();
	}

	public int getUnknownConnections() {
		return (int) clients.entrySet().stream().filter(e -> !e.getValue().receivedWelcome()).count();
	}

	public int getChannelCount() {
		return 0;
		// TODO
	}

	public Client getClient(String nick) {
		return clients.get(nick);
	}

	public String getInfo() {
		return "FOO BAR";
	}
}
