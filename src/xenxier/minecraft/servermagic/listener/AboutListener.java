package xenxier.minecraft.servermagic.listener;

import xenxier.minecraft.servermagic.Reference;
import xenxier.minecraft.servermagic.Server;

public class AboutListener extends Listener {

	/*
	 * Hard-coded about listener, an option to disable this from config.json 
	 * will be provided in the future.
	 */
	
	public AboutListener(Server server) {
		super(
			server, "!about",
			"/tellraw @$ [{\"text\":\"This server is currently running \",\"color\":\"darkgray\",\"italic\":\"true\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Created by Benjamin Gwynn (Xenxier)\\n\",\"color\":\"aqua\"},{\"text\":\"Click here to open the project's homepage on GitHub\",\"color\":\"gray\"}]}}},{\"text\":\"ServerMagic " + Reference.version + "\",\"color\":\"aqua\",\"bold\":\"true\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://github.com/benjamingwynn/mc-servermagic\"}}]"
		);
	}

}
