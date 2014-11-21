package xenxier.minecraft.servermagic.event;

import xenxier.minecraft.servermagic.Server;

public class LoginEvent extends Event {
	
	public LoginEvent(Server server) {
		super(server, "joined the game");
	}
	
	@Override
	String getArgument(String line) {
		String[] str = line.split(this.eventname)[0].split("\\s+");
		return str[str.length - 1];
	}

	@Override
	public void activity(String player) {
		server.passCommand("tell " + player + " Welcome to " + server.server_name + ", " + player + "!");
		server.passCommand("execute " + player + " ~ ~ ~ summon FireworksRocketEntity ~ ~ ~ {LifeTime:20,FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[65535,16777215],FadeColors:[18844]}]}}}}");
	}
}
