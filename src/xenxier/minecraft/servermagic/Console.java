package xenxier.minecraft.servermagic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
	public static int selected_server;

	public static void console() throws IOException, InterruptedException {
		
		/*
		 * Console
		 * 
		 * This is a basic command line UI for ServerMagic. It isn't finished, and is
		 * only temporary. This class is static and it isn't easily expandable.
		 * 
		 * There are no plans for a GUI, just a better command line interface.
		 */
		
		selected_server = 0; // default server is server 0
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) { // this is a sad panda. fix this.
			// Get line:
			String line = br.readLine().trim();
			if (line.equals("list")) {
				for (int i = 0; i < Activity.servers.size(); i++) {
					if (Activity.threads.size() <= i) {
						System.out.println(Activity.servers.get(i).server_id + ": " + Activity.servers.get(i).server_name + " | " + "DEAD");
					} else if (Activity.threads.get(i).isAlive() && Activity.servers.get(i).server_id == selected_server) {
						System.out.println(Activity.servers.get(i).server_id + ": " + Activity.servers.get(i).server_name + " | " + "ALIVE [SELECTED]");
					} else if (Activity.threads.get(i).isAlive()) {
						System.out.println(Activity.servers.get(i).server_id + ": " + Activity.servers.get(i).server_name + " | " + "ALIVE");
					} else if (!Activity.threads.get(i).isAlive()) {
						System.out.println(Activity.servers.get(i).server_id + ": " + Activity.servers.get(i).server_name + " | " + "STOPPED");
					}
				}
			} else if (!line.startsWith("select") && selected_server < 0) {
				System.out.println("Please select a server with 'select <server number>' before continuing.");
				System.out.println("You can also do 'list' to display all servers.");
			} else if (line.equals("exit")) {
				// Kill all servers (one by one):
				for (int i = 0; i < Activity.servers.size(); i++) {
					// Ask the server to stop:
					Activity.servers.get(i).passCommand("stop");
					
					// Wait here until the server stopped:
					while (Activity.threads.get(i).isAlive()){Thread.sleep(500);};
				}
				
				// Exit here:
				System.out.println("Done, all servers were stopped. Thanks for using ServerMagic <3");
				br.close();
				System.exit(0);
			} else if (line.startsWith("select")) {
				if (line.startsWith("select") && selected_server == Integer.valueOf(line.replaceAll("select", "").trim())) {
					System.out.println("Cannot select an already selected server.");
				} else if (line.startsWith("select") && Integer.valueOf(line.replaceAll("select", "").trim()) >= Activity.servers.size()) {
					System.out.println("That server doesn't exist in the config.json file.");
				} else if (line.startsWith("select") && Integer.valueOf(line.replaceAll("select", "").trim()) < 0) {
					System.out.println("You cannot select a negative server number.");
				} else {
					// actually select:
					selected_server = Integer.valueOf(line.replaceAll("select", "").trim());

					// Start a stopped sever:
					if (selected_server > -1 && selected_server < Activity.servers.size() && (Activity.threads.size() <= selected_server || !Activity.threads.get(selected_server).isAlive())) {
						System.out.println("Starting server...");
						Thread thread = new Thread(Activity.servers.get(selected_server));
						thread.start();
						Activity.threads.add(thread);
					}
				}
			} else {
				// Stop logic:
				if (line.equals("stop")) {
					// Ask the server to stop:
					Activity.servers.get(selected_server).passCommand("stop");
					
					// Wait here until the server stopped:
					while (Activity.threads.get(selected_server).isAlive()) {Thread.sleep(500);};
					
					// Remove the server from the thread list:
					Activity.threads.remove(selected_server);
					
					// Change server:
					selected_server = -1;
					System.out.println("Server was stopped. Use 'select <server number>' to continue.");
				}
				
				// Pass command:
				if (selected_server > -1 && selected_server < Activity.servers.size()) {
					Activity.servers.get(selected_server).passCommand(line);
				}
			}
		}
	}
}
