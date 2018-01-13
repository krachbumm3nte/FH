package kw47;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import kw43.Actor;
import kw45.TcpSocket;
import kw46.Transceiver;

public class Client implements Actor {
	int ID;
	Transceiver transceiver;
	String nick, user, name, host;
	IRCServer server;
	private boolean receivedWelcome, isOP;
	private List<String> joinedChannels;

	public Client(Socket s, IRCServer server, String nick) {
		try {
			this.transceiver = new Transceiver(new TcpSocket(s), this);
			transceiver.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.host = transceiver.getHost();
		this.nick = nick;
		user = null;
		name = null;
		this.server = server;
		receivedWelcome = false;
		isOP = false;
		joinedChannels = new LinkedList<String>();

	}

	@Override
	public void tell(String message, Actor sender) {
		System.out.println(nick + ": " + message);
		server.sendMessage(message, this);

	}

	public void sendMessage(String message) {
		System.out.println("sending to " + nick + ": " + message);
		transceiver.tell(message, this);
	}

	@Override
	public void shutdown() {
		transceiver.shutdown();
	}

	public void sendReply(int num, String arg) {
		switch (num) {
		case 001:
			sendMessage(String.format("Welcome to the Internet Relay Network <%s>!<%s>@<%s>", nick, user,
					server.getHost()));
			break;

		case 002:
			sendMessage(String.format("Your host is %s, running version %s", server.getHost(), server.getVersion()));
			break;

		case 003:
			sendMessage(String.format("This server was created %s", server.getCreated()));
			break;

		case 004:
			sendMessage(String.format("%s %s %s %s", server.getHost(), server.getVersion(), "ao", "mtov"));
			break;

		case 251:
			sendMessage(String.format(":There are %d users and %d services on %d servers", server.getUserCount(),
					server.getServiceCount(), server.getServerCount()));
			break;

		case 252:
			sendMessage(String.format("%d :operator(s) online", server.getOperatorCount()));
			break;

		case 253:
			sendMessage(String.format("%d :unknown connection(s)", server.getUnknownConnections()));
			break;

		case 254:
			sendMessage(String.format("%d :channels formed", server.getChannelCount()));
			break;

		case 255:
			sendMessage(String.format(":I have %d clients and %d servers", server.getClientCount(),
					server.getServerCount()));
			break;

		case 311:
			Client c = server.getClient(arg);
			sendMessage(String.format("%s %s %s * :%s", arg, c.getUser(), c.getHost(), c.getName()));
			break;

		case 312:
			sendMessage(String.format("%s %s :%s", arg, server.getHost(), server.getInfo()));
			break;

		case 318:
			sendMessage(arg + " :End of WHOIS list");
			break;
			
		case 331:
			sendMessage(arg + " :no topic is set.");
			break;
			
		case 332:
			sendMessage(arg);
			break;
			
		case 353:
			sendMessage(arg);
			break;
			
		case 366:
			sendMessage(arg + " :End of NAMES list");
			break;

		case 372:
			sendMessage(":- " + arg);
			break;

		case 375:
			sendMessage(String.format(":- %s Message of the day - ", arg));
			break;

		case 376:
			sendMessage(":End of MOTD command");
			break;

		case 401:
			sendMessage(String.format("%s :No such Nick/channel", arg));
			break;
			
		case 403:
			sendMessage(arg + " :No such channel");
			break;

		case 411:
			sendMessage(String.format(":No recipient given (%s)", arg));
			break;

		case 412:
			sendMessage(":No text to send");
			break;

		case 421:
			sendMessage(String.format("%s :Unknown commmand", arg));
			break;

		case 431:
			sendMessage(":No nickname given");
			break;

		case 433:
			sendMessage(arg + ":Nickname is already in use.");
			break;
			
		case 442:
			sendMessage(arg + ":You're not on that channel");
			break;

		case 451:
			sendMessage(":You have not registered");
			break;

		case 461:
			sendMessage(arg + ":Not enough parameters");
			break;

		case 462:
			sendMessage("Unauthorized command (already registered)");
			break;
			
		case 476:
			sendMessage(arg + " :Bad Channel Mask");
			break;

		default:
			break;
		}
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public String getFull() {
		return String.format(":%s!%s@%s", nick, user, host);
	}

	public void welcome() {
		receivedWelcome = true;
	}

	public boolean receivedWelcome() {
		return receivedWelcome;
	}

	public boolean isOP() {
		return isOP;
	}

	public void join(String channel) {
		joinedChannels.add(channel);
	}
	
	public boolean isOnChannel(String channel) {
		return joinedChannels.contains(channel);
	}
	
	public void leaveChannel(String channel) {
		joinedChannels.remove(channel);
	}
	
	public List<String> getJoinedChannels() {
		return joinedChannels;
	}
}
