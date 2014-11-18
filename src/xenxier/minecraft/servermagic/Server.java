package xenxier.minecraft.servermagic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.google.common.collect.Lists;

public class Server implements Runnable {
	public final String server_name;
	public final int server_id;
	public final Minecraft minecraft;
	public final List<String> launch_arguments;
	public final File server_dir;
	public final JSONObject server_json;
	
	public volatile String server_out;

	private ProcessBuilder server_proc_build;
	private Process server_proc;
	
	public Server(int id) {
		this.server_id = id;
		this.server_json = (JSONObject) Config.servers.get(id);
		
		this.server_name = server_json.get("name").toString();
		this.minecraft = new Minecraft(server_json.get("minecraft").toString());
		this.server_dir = new File(Reference.home_folder + File.separator + "servers" + File.separator + server_name);

		// Make sure our directory exists:
		if (!this.server_dir.exists()) {
			Logger.log("The server directory can't be found, creating it.");
			this.server_dir.mkdirs();
		}

		// EULA
		try {
			flagEulaTrue();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Build the launch commands:
		launch_arguments = Lists.newArrayList(
			Reference.java,
			"-jar",
			minecraft.getJarLocation().toString()
		);
		
		// Add from config:
		if (!(Config.global.get("arguments") == null)) {
			launch_arguments.add(Config.global.get("arguments").toString());
		}
		
		if (!(server_json.get("arguments") == null)) {
			launch_arguments.add(server_json.get("arguments").toString());
		}
		
		// Test:
		Logger.log("Set up server with the arguments: '" + launch_arguments + "'");
		
		try {
			overrideServerProperties();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void overrideServerProperties() throws IOException, InterruptedException {
		JSONObject propjson = (JSONObject) server_json.get("properties");
		
		if (propjson == null || propjson.isEmpty()) {
			return;
		}
		
		File propfile = new File(this.server_dir + File.separator + "server.properties");
		
		if (!propfile.exists()) {
			
			/*
			 * server.properties doesn't exist.
			 * 
			 * To fix this without dumping all of our overrides into the server.properties file
			 * or manually inserting default values into the file and overriding them we are
			 * going go create an instance of the server and wait until it creates the file, once
			 * it does we're just going to ask it to stop.
			 * 
			 */
			
			Logger.log("Creating server to generate properties.");
			
			// Create the server:
			ProcessBuilder procbuild = new ProcessBuilder(this.launch_arguments);
			procbuild.directory(this.server_dir);
			Process proc = procbuild.start();
			
			// Wait until server has created server.properties (check every half second)
			while(!propfile.exists()){Thread.sleep(500);}
			
			// Kill the server and log that we got the properties file
			proc.destroy();
			Logger.log("Server stopped. Properties were created.");
		}
		

		List<String> propfilelines = FileUtils.readLines(propfile, Charset.defaultCharset());
		
		for (int i = 0; i < propfilelines.size(); i++) {
			// check if this property is overridden in the JSON file
			if (propjson.get(propfilelines.get(i).split("=")[0]) != null) {
				replaceLine(propfile, propfilelines.get(i), propfilelines.get(i).split("=")[0] + "=" + propjson.get(propfilelines.get(i).split("=")[0]));
			}
		}
	}
	
	private static void replaceLine(File input_file, String line_to_remove, String replacement_line) throws IOException {
		System.out.println("REPLACING " + line_to_remove + " WITH " + replacement_line);
		
		File temp_file = new File(input_file.getAbsolutePath().toString() + ".tmp");
		BufferedReader reader = new BufferedReader(new FileReader(input_file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(temp_file));
		String line;
		
		while((line = reader.readLine()) != null) {
		    if (line.trim().equals(line_to_remove)) {
		    	writer.write(replacement_line + System.getProperty("line.separator"));
		    } else {
		    	writer.write(line + System.getProperty("line.separator"));
		    }
		}
		
		writer.close(); 
		reader.close(); 
		input_file.delete();
		temp_file.renameTo(input_file);
	}
	
	private void flagEulaTrue() throws IOException {
		/* 
		 * Automatically flag eula=true inside eula.txt.
		 * 
		 * > By running this function you are agreeing to Mojang's EULA.
		 * > https://account.mojang.com/documents/minecraft_eula
		 */

		BufferedWriter writer = new BufferedWriter(new FileWriter(this.server_dir + File.separator + "eula.txt"));
		writer.write("eula=true");
		writer.close();
	}

	@Override
	public synchronized void run() {
		Logger.log("Starting " + this.server_name);
		this.server_proc_build = new ProcessBuilder(this.launch_arguments);
		this.server_proc_build.directory(this.server_dir);
		
		try {
			this.server_proc = this.server_proc_build.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.server_proc.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
			   builder.append(line);
			   builder.append(System.getProperty("line.separator"));
			   Logger.serverLog(this.server_name, line.toString());
			   this.server_out = this.server_out + line.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
