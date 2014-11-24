package xenxier.minecraft.servermagic.console;

import xenxier.minecraft.servermagic.Activity;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop");
	}

	@Override
	public void execute() {
		// Wait here until the server stopped:
		while (Activity.threads.get(Console.current_server.server_id).isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		};
		
		// Remove the server from the thread list:
		Activity.threads.remove(Console.current_server.server_id);
		
		// No server should be selected:
		Console.unselectServer();
	}
}
