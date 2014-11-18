package xenxier.minecraft.servermagic;

public final class Logger {
	public static void log(Object object) {
		System.out.println("[XenCraft] " + object);
	}
	
	public static void serverLog(String serverid, String string) {
		System.out.println("[XenCraft:" + serverid + "] " + string);
	}
}
