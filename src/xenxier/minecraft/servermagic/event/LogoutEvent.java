package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

public class LogoutEvent extends Event {

	public LogoutEvent(Server server) {
		super(server, "left the game");
	}
	
	@Override
	String getArgument(String line) {
		String[] str = line.split(this.eventname)[0].split("\\s+");
		return str[str.length - 1];
	}
	
	@Override
	public void activity(String player) {
		// We can't really do anything visual here since the player no
		// longer has traceable co-ords.
	}

}
