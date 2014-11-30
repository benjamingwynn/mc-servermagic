package xenxier.minecraft.servermagic.console.command;

public class LogCommand extends Command {
	
	public static String log;

	public LogCommand() {
		super("log");
	}

	@Override
	public void execute() {
		System.out.println("Select what should log to the console.");
		System.out.println("Usage: log <current/all/none>");
	}
	
	@Override
	public void execute(String argument) {
		if (argument.equals("current") || argument.equals("all") || argument.equals("none")) {
			log = argument;
		} else {
			System.out.println("Usage: log <current/all/none>");
		}
	}

}
