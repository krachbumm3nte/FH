package kw47;

import java.io.IOException;
import java.net.Socket;

import kw43.Actor;
import kw45.TcpSocket;
import kw46.Transceiver;

public class Client implements Actor {
	int ID;
	Transceiver transceiver;
	String nick, user, name;
	IRCServer server;

	public Client(Socket s, IRCServer server, String nick) {
		try {
			this.transceiver = new Transceiver(new TcpSocket(s), this);
			transceiver.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.nick = nick;
		user = null;
		name = null;
		this.server = server;

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

	}
	
	public void sendReply(int num, String arg){
		switch (num) {
		case 001:
			sendMessage(String.format("Welcome to the Internet Relay Network <%s>!<%s>@<%s>", nick, user, server.getHost()));
			break;
		
		case 433:
			sendMessage(arg + ": Nickname is already in use.");
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

}
