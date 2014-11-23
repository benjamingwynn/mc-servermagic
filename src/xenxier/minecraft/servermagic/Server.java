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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.event.Event;
import xenxier.minecraft.servermagic.event.LoginEvent;
import xenxier.minecraft.servermagic.event.LogoutEvent;

import com.google.common.collect.Lists;

public class Server implements Runnable {
	public final String server_name;
	public final int server_id;
	public final Minecraft minecraft;
	public final List<String> server_args;
	public final File server_dir;
	public final JSONObject server_json;
	
	public volatile InputStream server_log;
	private volatile OutputStream server_in;
	private volatile BufferedWriter server_writer;
	private volatile BufferedReader server_reader;
	
	private final ArrayList<Event> server_events;

	private ProcessBuilder server_builder;
	private Process server_proc;
	
	public Server(int id) {
		this.server_id = id;
		this.server_json = (JSONObject) Config.servers.get(id);
		
		this.server_name = server_json.get("name").toString();
		this.minecraft = new Minecraft(server_json.get("minecraft").toString());
		this.server_dir = new File(Reference.home_folder + File.separator + "servers" + File.separator + server_name);
		
		// Register server events:
		this.server_events = new ArrayList<Event>();
		this.server_events.add(new LoginEvent(this));
		this.server_events.add(new LogoutEvent(this));

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
		if (Config.global != null && Config.global.get("arguments") != null) {
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
			Logger.log("We don't have any overrides for this server in our JSON file, but that's okay.", this);
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
			
			Logger.log("Creating server to generate properties...", this);
			
			// Create the server:
			ProcessBuilder procbuild = new ProcessBuilder(this.server_args);
			procbuild.directory(this.server_dir);
			Process proc = procbuild.start();
			
			// Wait until server has created server.properties (check every half second)
			while(!f.exists()){Thread.sleep(500);}
			
			// Kill the server and log that we got the properties file
			proc.destroy();
			Logger.log("Server stopped. server.properties was created.", this);
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
			
			/*
			 *  We have to do something with passCommand or Java will refuse to accept it as part
			 *  of our server process.
			 *  
			 *  A solution to this problem is to have an event on the server start, however having
			 *  an actual event class for something that can only happen once, and that doesn't have
			 *  a traditional trigger is difficult and would require a lot of changes and hacks in the
			 *  main event class. 
			 *  
			 *  What we do below is parse what's under servers/<server>/events/start like an event, and
			 *  keep it in the events object, but we don't actually have an event class for it.
			 *  
			 */
			
			if (this.server_json.get("events") != null && ((JSONObject) this.server_json.get("events")).get("start") != null) {
				Event.parse(this,((JSONObject) this.server_json.get("events")).get("start").toString());
			} else {
				passCommand("say ServerMagic started " + this.server_name);
			}
			
			// Backup objects:
			JSONObject backup_json = (JSONObject) this.server_json.get("backup");
			Backup backup = new Backup(this);
			
			// Backup runnable:
			if (this.server_json.get("backup") != null) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
				executor.scheduleAtFixedRate(new Runnable() {
				    public void run() {
				    	try {
							backup.backupServer();
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				}, 0, (long) backup_json.get("time"), TimeUnit.MINUTES);
			}
			
			// Server out:
			String line = null;
			StringBuilder out = new StringBuilder();
			
			// The following code loops while the server is alive.
			while ((line = this.server_reader.readLine()) != null) {
				out.append(line);
				out.append(System.getProperty("line.separator"));
				Logger.log(line.toString(), this);
				
				// Loop through events:
				for (int i = 0; i < this.server_events.size(); i++) {
					this.server_events.get(i).parseLine(line.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void passCommand(String command) {
		try {
			this.server_writer.write(command.trim() + "\n");
			this.server_writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
