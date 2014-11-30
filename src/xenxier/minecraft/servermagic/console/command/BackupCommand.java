package xenxier.minecraft.servermagic.console.command;

import java.io.IOException;

import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.Backup;
import xenxier.minecraft.servermagic.console.Console;

public class BackupCommand extends Command {

	public BackupCommand() {
		super("backup");
	}

	@Override
	public void execute() {
		System.out.println("Command for backing up the current server, ignoring scheduling.");
		System.out.println("Usage: backup <server/world>");
	}
	
	@Override
	public void execute(String argument) {
		/*
		 * Creating a new backup object here isn't ideal.
		 * TODO: Create a better way of doing the backup command.
		 */
		Backup backup = new Backup(Console.current_server);
		
		if ((JSONObject) Console.current_server.server_json.get("backup") != null) {
			try {
				if (argument.equals("server")) {
						backup.backupServer();
				} else if (argument.equals("world")) {
					try {
						backup.backupWorld();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Usage: backup <server/world>");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("This server doesn't carry the correct configuration for being backed up. Missing backup object from config.json.");
		}
	}
}
