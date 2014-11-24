package xenxier.minecraft.servermagic.console;


public class SelectCommand extends Command {

	public SelectCommand() {
		super("select");
	}

	@Override
	public void execute() {
		System.out.println("Usage: select <server number>");
	}
	
	@Override
	public void execute(String argument) {
		Console.selectServer(argument);
	}

}
