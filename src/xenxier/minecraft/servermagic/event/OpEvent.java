package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

public class OpEvent extends Event {
	
	public OpEvent(Server server) {
		super(server, "op", "opped");
	}
	
	@Override
	public String getArgument(String line) {
		String[] str = line.split(this.event)[1].split("\\s+");
		return str[str.length - 1];
	}
}
