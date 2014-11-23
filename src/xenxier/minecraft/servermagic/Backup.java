package xenxier.minecraft.servermagic;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import net.lingala.zip4j.core.ZipFile;
//import net.lingala.zip4j.exception.ZipException;
//import net.lingala.zip4j.model.ZipParameters;
//import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import xenxier.minecraft.servermagic.event.Event;

public final class Backup {
	private Server server;
	
	public Backup(Server server) {
		this.server = server;
	}

	public void backupServer() throws IOException {
		JSONObject backup_json = (JSONObject) this.server.server_json.get("backup");
		Event.parse(this.server, backup_json.get("start").toString());
		server.passCommand("save-off");
		server.passCommand("save-all");
		FileUtils.copyDirectory(server.server_dir, new File(Reference.home_folder + File.separator + "backup" + File.separator + this.server.server_name + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + File.separator + "server"));
		server.passCommand("save-on");
		Event.parse(this.server, backup_json.get("end").toString());
	}
	
//	public void backupWorld() throws IOException {
//		server.passCommand("say Backing up world.");
//		server.passCommand("save-off");
//		server.passCommand("save-all");
//		// Find the world file using server.properties:
//		String world = new MinecraftServerProperties(new File(server.server_dir + File.separator + "server.properties")).getValueOf("world");
//		FileUtils.copyDirectory(new File(server.server_dir + world), new File(Reference.home_folder + File.separator + "backup" + File.separator + this.server.server_name + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + File.separator + "world"));
//		server.passCommand("save-on");
//	}
	
//	public static void compressBackup(File backup_location) throws ZipException {
//		ZipFile zip = new ZipFile(backup_location + ".zip");
//		ZipParameters parameters = new ZipParameters();
//		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
//		zip.addFolder(backup_location, parameters);
//	}
}
