package xenxier.minecraft.servermagic;

import java.io.IOException;

public final class Main {
	public static void main(String args[]) {
		try {
			Config.createJSONObjects();
			Activity.startAllServers();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
