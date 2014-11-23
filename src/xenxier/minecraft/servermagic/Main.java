package xenxier.minecraft.servermagic;

import java.io.IOException;

public final class Main {
	public static void main(String args[]) {
		Logger.log("Welcome to ServerMagic " + Reference.version);
		try {
			Config.createJSONObjects();
			Activity.startAllServers();
			Console.console();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
