package xenxier.minecraft.servermagic.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import xenxier.minecraft.servermagic.Activity;
import xenxier.minecraft.servermagic.Reference;
import xenxier.minecraft.servermagic.Server;

public final class Console {
	public static Server current_server;
	public static ArrayList<Command> overrides = new ArrayList<Command>();
	public static ArrayList<Command> passives = new ArrayList<Command>();
	public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	public static void console() {
		// Welcome!
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("Welcome to ServerMagic " + Reference.version + "!");
		System.out.println("");
		System.out.println("ServerMagic now has support for different levels of logging, use");
		System.out.println("'log <current/all/none>' to show logs from the current server, all");
		System.out.println("servers or none of the servers.");
		System.out.println("");
		System.out.println("Note: As of ServerMagic 0.2, 'list' is now referenced to as 'listservers'");
		System.out.println("players.");
		System.out.println("");
		
		// Start at server 0 with logging off - TODO: JSON options for these defaults.
		selectServer(0);
		LogCommand.log = "none";
		
		// Register Passives:
		registerPassive(new xenxier.minecraft.servermagic.console.StopCommand());
		
		// Register Overrides:
		registerOverride(new xenxier.minecraft.servermagic.console.ExitCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.ListCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.LogCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.SelectCommand());
		
		// Input loop (I'm still not a fan of while true, but it seems to work well.)
		while (true) {
			System.out.print("ServerMagic " + xenxier.minecraft.servermagic.Reference.version + " > " + current_server.server_name + " (Server #" + current_server.server_id + ") > ");
			parseCommand(getInput());
		}
	}
	
	public static void parseCommand(String command) {
		// Test command for overrides:
		for (int i = 0; i < overrides.size(); i++) {
			if (command.contains(overrides.get(i).name)) {
				// If the user typed the command exactly:
				if (overrides.get(i).name.equals(command)) {
					overrides.get(i).execute();
					return; // return to not pass command
				// If the user typed the command with stuff behind it:
				} else if (command.split(" ").length > 1 && command.split(" ")[0].equals(overrides.get(i).name)) {
					overrides.get(i).execute(command.split(" ")[1]); // Get the argument and pass it
					return; // return to not pass command
				}
			}
		}
		
		// Pass command down to selected server:
		current_server.passCommand(command);
		
		// Test command for passives:
		for (int i = 0; i < passives.size(); i++) {
			if (command.contains(passives.get(i).name)) {
				passives.get(i).execute();
			}
		}
	}
	
	public static boolean selectServer(String server_number) {
		try {
			selectServer(Integer.parseInt(server_number));
			return true;
		} catch (NumberFormatException e) {
			// If the user can't type a number when asked to type a number:
			System.out.println("Please use a server number ranging from 0 to " + (Activity.servers.size() - 1) + ".");
			return false;
		}
	}
	
	public static void selectServer(int server_number) {
		try {
			current_server = Activity.servers.get(server_number);
			
			if (!current_server.server_thread.isAlive()) {
				System.out.println("The server was stopped, restarting...");
				
				if (current_server.server_thread.getState().toString().toLowerCase().equals("new")) {
					// If this is a new thread, start it:
					current_server.server_thread.start();
				} else {
					// If this is an old thread, recreate and restart it:
					current_server.server_thread = new Thread(current_server);
					current_server.server_thread.start();
				}
			}
			
		} catch (IndexOutOfBoundsException e) {
			// If server not found:
			System.out.println("Tried to select a server that doesn't exist.");
		}
	}
	
	public static void unselectServer() {
		boolean ok = false;
		while (!ok) {
			System.out.print("Please select a new server number > ");
			ok = selectServer(getInput());
		}
	}
	
	/* registerOverride
	 * 
	 * Stops command from being passed to selected server and instead
	 * does the command.
	 */
	
	private static String getInput() {
		try {
			return br.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void registerOverride(Command override) {
		overrides.add(override);
	}
	
	/* registerPassive
	 * 
	 * Passes the command to the selected server and also does extra
	 * code after the command has been passed.
	 */
	
	public static void registerPassive(Command passive) {
		passives.add(passive);
	}

}
