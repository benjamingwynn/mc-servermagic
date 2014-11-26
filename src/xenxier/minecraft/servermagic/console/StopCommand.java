package xenxier.minecraft.servermagic.console;

import xenxier.minecraft.servermagic.Activity;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop");
	}

	@Override
	public void execute() {
		// Wait here until the server stopped:
		while (Console.current_server.server_thread.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		};
		
		// No server should be selected:
		Activity.unselectServer();
	}
}
