package kw47;

public class Message {
	private String sender, cmd;
	private String[] args;

	public static enum MsgType {
		NICK, USER
	}

	public Message(String text) {
		args = new String[15];
		if (text.contains(":")) {
			int mark = text.indexOf(":");
			cmd = text.substring(mark + 1, text.length());
			args = text.substring(0, mark).split(" ");
		}

		else
			args = text.split(" ");
	}

	public boolean sendTo(Client c) {
		return false;
	}

	public String[] getArgs() {
		return args;
	}

	public String getSender() {
		return sender;
	}

	public String getCmd() {
		return cmd;
	}

}
