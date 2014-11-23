package xenxier.minecraft.servermagic.event;

import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.Server;

interface EventInterface {
	String getArgument(String line);
}

public abstract class Event implements EventInterface {
	String event;
	Server server;
	JSONObject eventjson;
	String eventname;
	
	/* This classes constructor has no modifier.
	*  Protected would allow subclasses outside of xenxier.minecraft.servermagic.event.
	*  Having no modifier only allows subclasses in the same package.
	*/
	
	Event(Server server, String eventname, String event) {
		this.server = server;
		this.event = event.toLowerCase();
		this.eventname = eventname;
		this.eventjson = (JSONObject) this.server.server_json.get("events");
	}
	
	public void parseLine(String line) {
		if (line.toLowerCase().contains(event)) {
			doJSONEvents(getArgument(line));
		}
	}
	
	private void doJSONEvents(String arg) {
		parse(this.server, this.event, arg);
	}
	
	public static void parse(Server server, String event, String arg) {
		String[] parsed = (event.replace("@$", arg)).split(";");
		for (int i = 0; i < parsed.length; i++) {
			server.passCommand(parsed[i]);
		}
	}
	
	public static void parse(Server server, String event) {
		String[] parsed = (event.replace("@$", "")).split(";");
		for (int i = 0; i < parsed.length; i++) {
			server.passCommand(parsed[i]);
		}
	}
}