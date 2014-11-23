package xenxier.minecraft.servermagic;

public final class Logger {
	public static void log(Object object) {
		System.out.println("[ServerMagic ("+ Reference.version + ")] " + object);
	}
	
	public static void log(String string, Server server) {
		System.out.println("[ServerMagic ("+ Reference.version + "):" + server.server_name + "] " + string);
	}
}
