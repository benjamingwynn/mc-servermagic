package xenxier.minecraft.servermagic.console;

interface CommandInterface {
	void execute();
}

public abstract class Command implements CommandInterface {
	public String name;
	
	public Command(String name) {
		this.name = name;
	}

	public void execute(String argument) {
		// By default, ignore arguments, this can be overridden using the @override annotation.
		execute();
	}
}
