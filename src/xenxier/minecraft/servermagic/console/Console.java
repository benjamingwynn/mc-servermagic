package xenxier.minecraft.servermagic.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import xenxier.minecraft.servermagic.Activity;
import xenxier.minecraft.servermagic.Reference;
import xenxier.minecraft.servermagic.Server;
import xenxier.minecraft.servermagic.console.command.Command;
import xenxier.minecraft.servermagic.console.command.LogCommand;

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
		System.out.println("Note: ServerMagic now has support for different levels of logging, use");
		System.out.println("'log <current/all/none>' to show logs from the current server, all");
		System.out.println("servers or none of the servers. By default ServerMagic logs none.");
		System.out.println("");
		System.out.println("Note: As of ServerMagic 0.2, 'list' is now referenced to as 'listservers'");
		System.out.println("to avoid conflict with the vanilla command that lists players.");
		System.out.println("");
		
		// Start at server 0 with logging off - TODO: JSON options for these defaults.
		Activity.selectServer(0);
		LogCommand.log = "none";
		
		// Register Passives:
		registerPassive(new xenxier.minecraft.servermagic.console.command.StopCommand());
		
		// Register Overrides:
		registerOverride(new xenxier.minecraft.servermagic.console.command.ExitCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.command.ListCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.command.LogCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.command.SelectCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.command.BackupCommand());
		registerOverride(new xenxier.minecraft.servermagic.console.command.RestoreWorldCommand());
		
		// Input loop (I'm still not a fan of while true, but it seems to work well.)
		while (true) {
			System.out.print("ServerMagic " + xenxier.minecraft.servermagic.Reference.version + " > " + current_server.server_name + " (Server #" + current_server.server_id + ") > ");
			parseCommand(getInput());
		}
	}
	
	private static void parseCommand(String command) {
		// Test command for overrides:
		for (int i = 0; i < overrides.size(); i++) {
			if (command.contains(overrides.get(i).name)) {
				// If the user typed the command exactly:
				if (overrides.get(i).name.equals(command)) {
					overrides.get(i).execute();
					return; // return to not pass command
				// If the user typed the command with stuff behind it:
				} else if (command.split(" ").length > 1 && command.split(" ")[0].equals(overrides.get(i).name)) {
					// Get the arguments:
					overrides.get(i).execute(Arrays.copyOfRange(command.split(" "), 1, command.split(" ").length));
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
	
	/* registerOverride
	 * 
	 * Stops command from being passed to selected server and instead
	 * does the command.
	 */
	
	public static String getInput() {
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
