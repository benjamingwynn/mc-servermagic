package xenxier.minecraft.servermagic.console;

import xenxier.minecraft.servermagic.Activity;

public class ListCommand extends Command {

	public ListCommand() {
		super("listservers");
	}

	@Override
	public void execute() {
		printList();
	}
	
	public static void printList() {
		System.out.println("\n" + "All Configured Servers:");
		
		int running = 0;
		final Object[][] table = new String[Activity.servers.size()][];
		
		for (int i = 0; i < Activity.servers.size(); i++) {
			String state = Activity.servers.get(i).server_thread.getState().toString().toLowerCase();
			table[i] = new String[] { String.valueOf(Activity.servers.get(i).server_id), Activity.servers.get(i).server_name, state.substring(0, 1).toUpperCase() + state.substring(1)};
			
			if (Activity.servers.get(i).server_thread.isAlive()) {
				running++;
			}
		}
		
		for (final Object[] row : table) {
		    System.out.format("%15s%15s%15s\n", row);
		}
		
		System.out.println("\n" + "Server #" + Console.current_server.server_id + " is curerntly selected.");
		System.out.println("There are " + Activity.servers.size() + " servers loaded. " + running + " of which are currently running." + "\n");
	}

}
