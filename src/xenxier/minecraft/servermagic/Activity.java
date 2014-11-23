package xenxier.minecraft.servermagic;

import java.util.ArrayList;

public final class Activity {
	public static final ArrayList<Thread> threads = new ArrayList<Thread>();
	public static final ArrayList<Server> servers = new ArrayList<Server>();

	public static void startAllServers() throws InterruptedException {
		// Start every server:
		for (int i = 0; i < Config.servers.size(); i++) {
			servers.add(new Server(i));
			Thread thread = new Thread(servers.get(i));
			thread.start();
			threads.add(thread);
		}
	}
}
