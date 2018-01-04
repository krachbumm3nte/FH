package kw47;

public class Message {
	private String sender, cmd;
	private String[] args;

	public Message(String text) {
		args = new String[15];
		if (text.contains(":")) {
			int mark = text.indexOf(":");
			cmd = text.substring(mark + 1, text.length());
			args = text.substring(0, mark).split(" ");
		}

		else {
			args = text.split(" ");
		}
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
	
	public boolean enoughParams(Client c, int count) {
		boolean enough = args.length -1 + (cmd != null ? 1: 0) >= count;
		System.out.println("enough = " + enough);
		if(enough == false) c.sendReply(461, args[0]);
		return enough;
	}

}
