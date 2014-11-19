package xenxier.minecraft.servermagic;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;

public final class Backup {
	public static void backupServer(Server server, File backup_location) throws IOException {
		server.passCommand("say Backing up server.");
		server.passCommand("save-off");
		server.passCommand("save-all");
		FileUtils.copyDirectory(server.server_dir, backup_location);
		server.passCommand("save-on");
	}
	
	public static void backupWorld(Server server, File backup_location) throws IOException {
		server.passCommand("say Backing up world.");
		server.passCommand("save-off");
		server.passCommand("save-all");
		FileUtils.copyDirectory(server.server_dir, backup_location);
		server.passCommand("save-on");
	}
	
	public static void compressBackup(File backup_location) throws ZipException {
		ZipFile zip = new ZipFile(backup_location + ".zip");
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zip.addFolder(backup_location, parameters);

	}
}
