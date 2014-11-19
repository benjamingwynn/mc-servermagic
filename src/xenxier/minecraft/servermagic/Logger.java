package xenxier.minecraft.servermagic;

public final class Logger {
	public static void log(Object object) {
		System.out.println("[ServerMagic] " + object);
	}
	
	public static void log(String serverid, String string) {
		System.out.println("[ServerMagic:" + serverid + "] " + string);
	}
}
