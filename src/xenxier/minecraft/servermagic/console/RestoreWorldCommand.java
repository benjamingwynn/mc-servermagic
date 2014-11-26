package xenxier.minecraft.servermagic.console;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.Activity;
import xenxier.minecraft.servermagic.MinecraftServerProperties;
import xenxier.minecraft.servermagic.Reference;
import xenxier.minecraft.servermagic.event.Event;

public class RestoreWorldCommand extends Command {
	private static final String USAGE = "Usage: rollback <time> <minutes/hours/days>";

	public RestoreWorldCommand() {
		super("rollback");
	}

	@Override
	public void execute() {
		System.out.println("Restores the current world from a ServerMagic backup closet to the amount of time requested");
		System.out.println(USAGE);
	}
	
	@Override
	public void execute(String arguments[]) {
		if (arguments.length < 2) {
			System.out.println("This command requires two arguments.");
			System.out.println(USAGE);
			return;
		}
		
		if (!(arguments[1].toLowerCase().equals("minutes") || arguments[1].toLowerCase().equals("hours") || arguments[1].toLowerCase().equals("days"))) {
			System.out.println("The second argument must be either minutes, hours or days. Was '" + arguments[1] + "'.");
			System.out.println(USAGE);
			return;
		}
		

		try {
			// Get a long as the time to go back from the string
			int time = Integer.parseInt(arguments[0]);
			
			// Get the second argument as a TimeUnit
			TimeUnit time_unit = TimeUnit.valueOf(arguments[1].toUpperCase());
			
			// Get folder listing
			File world_folder = new File(Reference.home_folder + File.separator + "backup" + File.separator + "worlds" + File.separator + Console.current_server.server_name + File.separator + new MinecraftServerProperties(new File(Console.current_server.server_dir + File.separator + "server.properties")).getValueOf("level-name"));
			String[] world_list = world_folder.list();
			
			// Create a calendar and remove a number of minutes based on our variables:
			Calendar cal = Calendar.getInstance();
			int time_in_minutes = (int) time_unit.toMinutes(time);
			cal.add(Calendar.MINUTE, 0 - time_in_minutes);
			
			// Get the ideal target world backup:
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
			format.setTimeZone(cal.getTimeZone());
			String target = format.format(cal.getTime());
			
			Long[] numerical_world_list = new Long[world_list.length];
			
			// Loop through our world backup list:
			for (int i = 0; i < world_list.length; i++) {
				if (world_list[i].equals(target)) {
					// If it equals target, restore:
					restoreWorld(target);
					return;
				}
				
				// Parse this into an integer and add it to our int world list
				numerical_world_list[i] = Long.parseLong(world_list[i].replace("_", ""));
			}
			
			// Get closest date tag:
			Long intDateTag = getLongInArrayClosestToNewLong(numerical_world_list, Long.parseLong(target.replace("_", "")));
			
			// Transform it into a string and re-add the underscore:
			String stringDateTag = String.valueOf(intDateTag);
			stringDateTag = new StringBuilder(stringDateTag).insert(8, "_").toString();
			
			// Restore:
			restoreWorld(stringDateTag);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void restoreWorld(String datetag) throws IOException, InterruptedException {
		// Get JSON:
		JSONObject backup_json = (JSONObject) ((JSONObject) Console.current_server.server_json.get("backup")).get("world-restore");
		
		if (backup_json.get("start") != null) {
			Event.parse(Console.current_server, backup_json.get("start").toString());
		} else {
			Console.current_server.passCommand("say Server will restart to complete world restore in 30 seconds.");
		}

		if (backup_json.get("time") != null) {
			Thread.sleep((long) backup_json.get("time") * 1000);
		} else {
			Thread.sleep(30 * 1000);
		}
		
		// Stop the server
		Console.current_server.passCommand("stop");

		while (Console.current_server.server_thread.isAlive()) {
			Thread.sleep(500);
		};
		
		// Replace the world:
		String world = new MinecraftServerProperties(new File(Console.current_server.server_dir + File.separator + "server.properties")).getValueOf("level-name");
		FileUtils.deleteDirectory(new File(Console.current_server.server_dir + File.separator + world));
		FileUtils.copyDirectory(new File(Reference.home_folder + File.separator + "backup" + File.separator + "worlds" + File.separator + Console.current_server.server_name + File.separator + world + File.separator + datetag), new File(Console.current_server.server_dir + File.separator + world));
		
		// Start the server (selecting a dead server with true restarts it and hides the restart messages):
		Activity.selectServer(Console.current_server.server_id, true);
		
		if (backup_json != null) {
			Event.parse(Console.current_server, backup_json.get("end").toString());
		}
		
		System.out.println("Restored world " + world + " from backup " + datetag + " succesfully.");
	}
	
	private static long getLongInArrayClosestToNewLong(Long[] numerical_world_list, long myNumber) {
		long distance = Math.abs(numerical_world_list[0] - myNumber);
		int idx = 0;
		for(int c = 1; c < numerical_world_list.length; c++){
		    long cdistance = Math.abs(numerical_world_list[c] - myNumber);
		    if(cdistance < distance){
		        idx = c;
		        distance = cdistance;
		    }
		}
		return numerical_world_list[idx];
	}
}
