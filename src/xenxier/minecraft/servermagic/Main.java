package xenxier.minecraft.servermagic;

import java.io.IOException;
import java.util.Calendar;

import xenxier.minecraft.servermagic.console.Console;

public final class Main {
	public static void main(String args[]) {
		try {
			System.out.println(Calendar.YEAR);
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
