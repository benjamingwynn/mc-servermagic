package xenxier.minecraft.servermagic.console;

import java.io.IOException;

import xenxier.minecraft.servermagic.Activity;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit");
	}

	@Override
	public void execute() {
		// Kill all servers (one by one):
		for (int i = 0; i < Activity.servers.size(); i++) {
			// Ask the server to stop:
			Activity.servers.get(i).passCommand("stop");
			
			// Wait here until the server stopped:
			while (Activity.threads.get(i).isAlive()){
				try { Thread.sleep(500); } catch (InterruptedException e) { }
			};
		}
		
		// Exit here:
		System.out.println("Done, all servers were stopped. Thanks for using ServerMagic <3");
		try { Console.br.close(); } catch (IOException e) { System.exit(1); }
		System.exit(0);
	}
	
}
