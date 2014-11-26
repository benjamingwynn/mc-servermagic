package xenxier.minecraft.servermagic;

import java.util.ArrayList;

import xenxier.minecraft.servermagic.console.Console;
import xenxier.minecraft.servermagic.console.ListCommand;

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
	
	public static void restartServer(int server_id) {
		servers.get(server_id).passCommand("stop");
		selectServer(server_id);
	}
	
	public static void selectServer(int server_id) {
		selectServer(server_id, false);
	}
	
	public static void selectServer(int server_id, boolean hide_log) {
		try {
			Console.current_server = Activity.servers.get(server_id);
			
			if (!Console.current_server.server_thread.isAlive()) {
				if (!hide_log) {
					System.out.println("The server was stopped, restarting...");
				}
				
				if (Console.current_server.server_thread.getState().toString().toLowerCase().equals("new")) {
					// If this is a new thread, start it:
					Console.current_server.server_thread.start();
				} else {
					// If this is an old thread, recreate and restart it:
					Console.current_server.server_thread = new Thread(Console.current_server);
					Console.current_server.server_thread.start();
				}
			}
			
		} catch (IndexOutOfBoundsException e) {
			// If server not found:
			System.out.println("Tried to select a server that doesn't exist.");
		}
	}
	
	public static boolean selectServer(String server_number) {
		try {
			Activity.selectServer(Integer.parseInt(server_number));
			return true;
		} catch (NumberFormatException e) {
			// If the user can't type a number when asked to type a number:
			System.out.println("Please use a server number ranging from 0 to " + (Activity.servers.size() - 1) + ".");
			return false;
		}
	}
	
	public static void unselectServer() {
		ListCommand.printList();
		
		boolean ok = false;
		while (!ok) {
			System.out.print("Please select a new server number > ");
			ok = selectServer(Console.getInput());
		}
	}
}
