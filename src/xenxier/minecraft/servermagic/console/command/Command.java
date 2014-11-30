package xenxier.minecraft.servermagic.console.command;

interface CommandInterface {
	void execute();
}

public abstract class Command implements CommandInterface {
	public String name;
	
	public Command(String name) {
		this.name = name;
	}

	// By default, convert a string of arguments into a single argument. Override in sub-class with @Override
	public void execute(String[] arguments) {
		execute(arguments[0]);
	}

	// By default, ignore an argument, this can be overridden using the @Override annotation.
	public void execute(String argument) {
		execute();
	}
}
