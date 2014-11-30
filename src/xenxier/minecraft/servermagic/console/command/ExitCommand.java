package xenxier.minecraft.servermagic.console.command;

import java.io.IOException;

import xenxier.minecraft.servermagic.Activity;
import xenxier.minecraft.servermagic.console.Console;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit");
	}

	@Override
	public void execute() {
		// Tell the user we're exiting now:
		System.out.println("Stopping all remaining servers...");
		
		// Kill all servers (one by one):
		for (int i = 0; i < Activity.servers.size(); i++) {
			// Ask the server to stop:
			Activity.servers.get(i).passCommand("stop");
			
			// Wait here until the server stopped:
			while (Activity.servers.get(i).server_thread.isAlive()){
				try { Thread.sleep(500); } catch (InterruptedException e) { }
			};
		}
		
		// Exit here:
		System.out.println("Done, all servers were stopped. Thanks for using ServerMagic <3");
		try {
			Console.br.close();
			System.exit(0);
		} catch (IOException e) {
			System.exit(1);
		}
		
	}
	
}
