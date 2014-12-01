package xenxier.minecraft.servermagic.listener;

import xenxier.minecraft.servermagic.Server;
import xenxier.minecraft.servermagic.event.Event;

public class Listener {
	String listener;
	Server server;
	String to_execute;
	
	public Listener(Server server, String listen_to_what, String do_what_on_listen) {
		this.server = server;
		this.listener = listen_to_what.trim().toLowerCase();
		this.to_execute = do_what_on_listen;
	}
	
	/*  Parse '[12:53:39] [Server thread/INFO]: <Player> !test' into 'Player' and 'test',
	 *  and execute the listener if requested.
	 */
	
	public final void parseLog(String log_line) {
		// Check to see if the line was chat-message-like:
		if ((log_line.split("<").length > 1) && (log_line.split(">").length > 1)) {
		
			// Remove log stuff:
			String clear_line = log_line.split("<")[1].trim();
			
			// Listen is everything after '>'
			String listened = clear_line.split(">")[1].trim();
			
			// Is listened listener?
			if (listened.equals(this.listener)) {
				// Player is everything before '>'
				String player = clear_line.split(">")[0];
				
				this.execute(player);
			}
		
		}
	}
	
	public void execute(String player) {
		Event.parse(this.server, to_execute, player); // pass to static event parser
	}
}