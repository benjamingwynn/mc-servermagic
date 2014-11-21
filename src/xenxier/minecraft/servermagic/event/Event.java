package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

interface EventInterface {
	public void activity(String argument);
}

public abstract class Event implements EventInterface {
	String eventname;
	Server server;
	
	/* This classes constructor has no modifier.
	*  Protected would allow subclasses outside of xenxier.minecraft.servermagic.event.
	*  Having no modifier only allows subclasses in the same package.
	*/
	
	Event(Server server, String eventname) {
		this.server = server;
		this.eventname = eventname.toLowerCase();
	}
	
	public void parseLine(String line) {
		if (line.toLowerCase().contains(eventname)) {
			activity(getArgument(line));
		}
	}
	
	String getArgument(String line) {
		return "";
	}
}