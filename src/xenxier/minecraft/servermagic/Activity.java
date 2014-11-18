package xenxier.minecraft.servermagic;

import java.util.ArrayList;

public final class Activity {
	public static final ArrayList<Thread> threads = new ArrayList<Thread>();

	public static void startAllServers() {
		// start every server in the background:
		for (int i = 0; i < Config.servers.size(); i++) {
			Runnable task = new Server(i);
			Thread worker = new Thread(task);
			worker.start();
			threads.add(worker);
		}
	}
}
