package xenxier.minecraft.servermagic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
	public static void console() throws IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (Activity.threads.get(0).isAlive()) {
			String input = br.readLine();
			System.out.println("servermagic> Server #1 > " + input);
			Activity.servers.get(0).passCommand(input);
		}
	}
}
