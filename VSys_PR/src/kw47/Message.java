package kw47;

import java.util.Arrays;

public class Message {
	private String sender, text, command;
	private String[] args;

	public Message(String string) {
		args = new String[15];
		if (string.contains(":")) {
			int mark = string.indexOf(":");
			text = string.substring(mark + 1, string.length());
			String[] tempArgs = string.substring(0, mark).split(" ");
			args = Arrays.copyOfRange(tempArgs, 1, tempArgs.length);
			command = tempArgs[0];
		}

		else {
			String[] tempArgs = string.split(" ");
			args = Arrays.copyOfRange(tempArgs, 1, tempArgs.length);
			command = tempArgs[0];
		}
	}

	public String getCommand() {
		return command;
	}

	public String[] getArgs() {
		return args;
	}

	public String getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	public boolean hasEnoughParams(int count) {
		return args.length + (text != null ? 1 : 0) >= count;

	}

}
