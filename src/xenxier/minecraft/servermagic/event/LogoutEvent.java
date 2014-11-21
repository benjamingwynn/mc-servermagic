package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

public class LogoutEvent extends Event {

	public LogoutEvent(Server server) {
		super(server, "logout", "left the game");
	}
	
	@Override
	public String getArgument(String line) {
		String[] str = line.split(this.event)[0].split("\\s+");
		return str[str.length - 1];
	}
	
	public void activity(String player) {
		// We can't really do anything visual here since the player no
		// longer has traceable co-ords.
	}

}
