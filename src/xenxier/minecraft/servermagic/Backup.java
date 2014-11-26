package xenxier.minecraft.servermagic;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.event.Event;

public final class Backup {
	private Server server;
	private JSONObject backup_json;
	
	public Backup(Server server) {
		this.server = server;
		this.backup_json = (JSONObject) this.server.server_json.get("backup");
	}

	public void backupServer() throws IOException {
		JSONObject json = (JSONObject) this.backup_json.get("server");
		if (json != null && json.get("start") != null) {
			Event.parse(this.server, json.get("start").toString());
		}
		server.passCommand("save-off");
		server.passCommand("save-all");
		FileUtils.copyDirectory(server.server_dir, new File(Reference.home_folder + File.separator + "backup" + File.separator + "servers" + File.separator + this.server.server_name + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
		server.passCommand("save-on");
		if (json != null && json.get("end") != null) {
			Event.parse(this.server, json.get("end").toString());
		}
	}
	
	public void backupWorld() throws IOException {
		JSONObject json = (JSONObject) this.backup_json.get("world");
		
		if (json != null && json.get("start") != null) {
			Event.parse(this.server, json.get("start").toString());
		}
		
		server.passCommand("save-off");
		server.passCommand("save-all");
		
		// Find the world file using server.properties:
		String world = new MinecraftServerProperties(new File(server.server_dir + File.separator + "server.properties")).getValueOf("level-name");
		FileUtils.copyDirectory(new File(server.server_dir + File.separator + world), new File(Reference.home_folder + File.separator + "backup" + File.separator + "worlds" + File.separator + this.server.server_name + File.separator + world + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
		
		server.passCommand("save-on");
		
		if (json != null && json.get("end") != null) {
			Event.parse(this.server, json.get("end").toString());
		}
	}
}
