package xenxier.minecraft.servermagic;

import java.util.ArrayList;

public final class Activity {
	public static final ArrayList<Server> servers = new ArrayList<Server>();

	public static void startAllServers() throws InterruptedException {
		for (int i = 0; i < Config.servers.size(); i++) {
			// Add every server to an ArrayList
			servers.add(new Server(i));
			
			// Start every server:
			servers.get(i).server_thread.start();
		}
	}
}
