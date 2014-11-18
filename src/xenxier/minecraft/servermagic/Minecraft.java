package xenxier.minecraft.servermagic;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Minecraft {
	public final String minecraft_version;
	public final File minecraft_jar;
	
	public Minecraft(String ver) {
		minecraft_version = ver;
		minecraft_jar = new File(Reference.home_folder + File.separator + ver + ".jar");
		
		// If we don't have the jar then download it:
		if (!getJarLocation().exists()) {
			try {
				DownloadMinecraft();
			} catch (IOException e) {
				// Couldn't download Minecraft:
				Logger.log("Couldn't get Minecraft version " + this.minecraft_version + " :(");
				e.printStackTrace();
			}
		}
	}
	
	private void DownloadMinecraft() throws MalformedURLException, IOException {
		Logger.log("Downloading Minecraft version " + this.minecraft_version + "...");
		org.apache.commons.io.FileUtils.copyURLToFile(
			new URL("https://s3.amazonaws.com/Minecraft.Download/versions/" + this.minecraft_version + "/minecraft_server." + this.minecraft_version + ".jar"),
			getJarLocation()
		);
		Logger.log("Downloaded " + this.minecraft_version + " successfully.");
	}
	
	public File getJarLocation() {
		return new File(Reference.home_folder + File.separator + "mc_jars" + File.separator + this.minecraft_version + ".jar");
	}
}
