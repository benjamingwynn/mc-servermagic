package xenxier.minecraft.servermagic.console;

import xenxier.minecraft.servermagic.Activity;

public class ListCommand extends Command {

	public ListCommand() {
		super("list");
	}

	@Override
	public void execute() {
		System.out.println("\nAll Configured Servers:");
		
		int running = 0;
		final Object[][] table = new String[Activity.servers.size()][];
		
		for (int i = 0; i < Activity.servers.size(); i++) {
			if (Activity.threads.size() <= i) {
				table[i] = new String[] { String.valueOf(Activity.servers.get(i).server_id), Activity.servers.get(i).server_name, "Stopped", "(Thread Remains)"};
			} else if (Activity.threads.get(i).isAlive() && Activity.servers.get(i).server_id == Console.current_server.server_id) {
				running++;
				table[i] = new String[] { String.valueOf(Activity.servers.get(i).server_id), Activity.servers.get(i).server_name, "Running", "Selected"};
			} else if (Activity.threads.get(i).isAlive()) {
				running++;
				table[i] = new String[] { String.valueOf(Activity.servers.get(i).server_id), Activity.servers.get(i).server_name, "Running"};
			} else if (!Activity.threads.get(i).isAlive()) {
				table[i] = new String[] { String.valueOf(Activity.servers.get(i).server_id), Activity.servers.get(i).server_name, "Stopped"};
			}
		}
		
		for (final Object[] row : table) {
		    System.out.format("%15s%15s%15s\n", row);
		}
		
		System.out.println("\nThere are " + Activity.servers.size() + " servers loaded. " + running + " of which are currently running.\n");
	}

}
