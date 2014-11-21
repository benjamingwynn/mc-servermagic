package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

public class LoginEvent extends Event {
	
	public LoginEvent(Server server) {
		super(server, "login", "joined the game");
	}
	
	@Override
	public String getArgument(String line) {
		String[] str = line.split(this.event)[0].split("\\s+");
		return str[str.length - 1];
	}
}
