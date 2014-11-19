package xenxier.minecraft.servermagic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MinecraftServerProperties {
	public final File file;
	public final List<String> lines;
	
	public MinecraftServerProperties(File server_properties_file) throws IOException {
		this.file = server_properties_file;
		this.lines = FileUtils.readLines(file, Charset.defaultCharset());
	}
	
	public String getPropOf(String line) {
		return getPropLine(line).split("=")[0];
	}
	
	public String getValueOf(String line) {
		return getPropLine(line).split("=")[1];
	}
	
	public String getPropOf(Integer line) {
		return lines.get(line).split("=")[0];
	}
	
	public String getValueOf(Integer line) {
		return lines.get(line).split("=")[1];
	}

	public String getPropLine(String property) {
		return lines.get(getPropLineNumber(property));
	}
	
	public int getPropLineNumber(String property) {
		for (int i = 0; i < lines.size(); i++) {
			if (doesPropExistHere(property, i)) {
				return i;
			}
		}
		return -1;
	}

	public boolean doesPropExistHere(String prop, int line) {
		return this.lines.get(line).split("=")[0].equals(prop);
	}

	public void modifyProp(String property, String value) throws IOException {
		replaceLine(getPropLine(property), property + "=" + value);
	}
	
	private void replaceLine(String old_line, String new_line) throws IOException {
		System.out.println("REPLACE " + old_line + ":" + new_line);
		String line;
		File tmp = new File(this.file.getAbsolutePath().toString() + ".tmp");
		BufferedReader reader = new BufferedReader(new FileReader(this.file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
		
		while((line = reader.readLine()) != null) {
		    if (line.trim().equals(old_line)) {
		    	writer.write(new_line + System.getProperty("line.separator"));
		    } else {
		    	writer.write(line + System.getProperty("line.separator"));
		    }
		}
		
		writer.close();
		reader.close(); 
		file.delete();
		tmp.renameTo(this.file);
	}
}
