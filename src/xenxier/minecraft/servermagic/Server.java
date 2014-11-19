package xenxier.minecraft.servermagic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.common.collect.Lists;

public class Server implements Runnable {
	public final String server_name;
	public final int server_id;
	public final Minecraft minecraft;
	public final List<String> server_args;
	public final File server_dir;
	public final JSONObject server_json;
	
	private volatile InputStream server_log;
	private volatile OutputStream server_in;
	private volatile BufferedWriter server_writer;
	private volatile BufferedReader server_reader;

	private ProcessBuilder server_builder;
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
		server_args = Lists.newArrayList(
			Reference.java,
			"-jar",
			minecraft.getJarLocation().toString()
		);
		
		// Add from config:
		if (!(Config.global.get("arguments") == null)) {
			server_args.add(Config.global.get("arguments").toString());
		}
		
		if (!(server_json.get("arguments") == null)) {
			server_args.add(server_json.get("arguments").toString());
		}
		
		// Test:
		Logger.log("Set up server with the arguments: '" + server_args + "'");
		
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
			Logger.log("We don't have any overrides for this server in our JSON file, but that's okay.", this.server_name);
			return;
		}
		
		File f = new File(this.server_dir + File.separator + "server.properties");
		
		if (!f.exists()) {
			
			/*
			 * server.properties doesn't exist.
			 * 
			 * To fix this without dumping all of our overrides into the server.properties file
			 * or manually inserting default values into the file and overriding them, we are
			 * going go create an instance of the server and wait until it creates the file, once
			 * it does we're just going to ask it to stop.
			 * 
			 */
			
			Logger.log("Creating server to generate properties...", this.server_name);
			
			// Create the server:
			ProcessBuilder procbuild = new ProcessBuilder(this.server_args);
			procbuild.directory(this.server_dir);
			Process proc = procbuild.start();
			
			// Wait until server has created server.properties (check every half second)
			while(!f.exists()){Thread.sleep(500);}
			
			// Kill the server and log that we got the properties file
			proc.destroy();
			Logger.log("Server stopped. server.properties was created.", this.server_name);
		}
		
		MinecraftServerProperties propmc = new MinecraftServerProperties(f);
		
		// Loop through all properties in server.properties
		for (int i = 0; i < propmc.lines.size(); i++) {
			// If the property found exists in the JSON file:
			if (propjson.get(propmc.getPropOf(i)) != null) {
				propmc.modifyProp(propmc.getPropOf(i), propjson.get(propmc.getPropOf(i)).toString());
			}
		}
	}
	
	private void flagEulaTrue() throws IOException {
		/* 
		 * Automatically flag eula=true inside eula.txt.
		 * 
		 * > By executing this method you are agreeing to Mojang's EULA.
		 * > https://account.mojang.com/documents/minecraft_eula
		 */

		BufferedWriter writer = new BufferedWriter(new FileWriter(this.server_dir + File.separator + "eula.txt"));
		writer.write("eula=true");
		writer.close();
	}

	@Override
	public synchronized void run() {
		Logger.log("Starting " + this.server_name);
		this.server_builder = new ProcessBuilder(this.server_args);
		this.server_builder.directory(this.server_dir);
		this.server_builder.redirectErrorStream(true);
		
		try {
			this.server_proc = this.server_builder.start();
			this.server_in = this.server_proc.getOutputStream();
			this.server_log = this.server_proc.getInputStream();
			this.server_writer = new BufferedWriter(new OutputStreamWriter(server_in));
			this.server_reader = new BufferedReader(new InputStreamReader(server_log));
			
			passCommand("say ServerMagic started the server"); // Java requires us to pass a command so we can use passCommand later.

			String line = null;
			StringBuilder out = new StringBuilder();
			
			while ((line = this.server_reader.readLine()) != null) {
			   out.append(line);
			   out.append(System.getProperty("line.separator"));
			   Logger.log(this.server_name, line.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void passCommand(String command) {
		try {
			this.server_writer.write(command + "\n");
			this.server_writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
