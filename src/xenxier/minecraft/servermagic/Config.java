package xenxier.minecraft.servermagic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class Config {
	public static JSONObject json;
	public static JSONArray servers;
	public static JSONObject global;
	
	@SuppressWarnings("unchecked") // shush.
	public static void createJSONObjects() throws IOException {
		File json_file = new File(Reference.home_folder + File.separator + "config.json");
		
		if (!json_file.exists()) {
			json_file.getParentFile().mkdirs();
			json_file.createNewFile();
			
			JSONObject head = new JSONObject();
			JSONObject global = new JSONObject();
			JSONArray servers = new JSONArray();
			JSONObject server = new JSONObject();

			server.put("name", "MyServer");
			server.put("minecraft", "1.8");
			servers.add(server);
			
			global.put("arguments", "nogui");

			head.put("global", global);
			head.put("servers", servers);
			
			FileWriter writer = new FileWriter(json_file);
			head.writeJSONString(writer);
			writer.close();
		}
		
		try {
			FileReader reader = new FileReader(json_file);
			JSONParser jsonParser = new JSONParser();
			Config.json = (JSONObject) jsonParser.parse(reader);
			Config.servers = (JSONArray) Config.json.get("servers");
			Config.global = (JSONObject) Config.json.get("global");
			
			if (Config.servers.get(0) == null) {
				Logger.log("At least one server must be in your config.json file to continue.");
				System.exit(1);
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
	}
}
