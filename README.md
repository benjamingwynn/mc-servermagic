# ServerMagic

A Java application to download, start, stop, maintain, backup and configure any number of Minecraft servers from a single JSON file.

## Basic Usage

To use ServerMagic just start up the complied JAR file in a terminal with something like `java -jar servermagic.jar`

If you wish to configure your servers then open up .servermagic/config.json relative to where you opened the ServerMagic JAR. The file should be pretty self explanatory for those familiar with both Minecraft servers and JSON, but there are a few rules:

1. Anything inside of the properties object will modify what is in server.properties, **but it will never add anything new**, only override what is already there.
2. Minecraft versions *should* be a string, since Mojang's versioning system includes '1.8.1-pre1' and '14w32b', which are not numerical values.

ServerMagic will automatically download and run Minecraft.

Here's an example of a ServerMagic config:

```JSON
{
	"global": {
		"arguments": "nogui"
	}
	"servers" [
		{
			"name": "HelloWorld",
			"minecraft": "1.7.2",
			"arguments": "-Xmx 1G",
			"properties": {
				"server-port": "12345",
				"motd": "Test Server",
				"level-name": "Test_World"
			}
		}
		{
			"name": "AReallyBasicEntry",
			"minecraft": "1.6.2"
		}
		{
			"name": "Creative",
			"minecraft": "1.7.10",
			"properties": {
				"view-distance": "16",
				"gamemode": "1",
				"server-port": "25564",
				"motd": "Creative Server"
			}
		}
	]
}
```

## Events

ServerMagic now also supports automated events.

Events are called under the 'events' object under a server, and are simply server commands to execute when something happens in the log.

The following events are currently supported:

* `start` - called when the server is started.
* `login` - called when a player logs in.
* `logout` - called when a player logs out.

Multiple server commands can be executed, and are separated with the `;` character. When the `@$` characters are used consecutively, events that specify a player (such as someone logging out) will replace `@$`. For example, `say Hello @$` will be replaced with `say Hello Notch` if someone named Notch were to log on.

```JSON
{
	"servers" [
		{
			"name": "EventServer",
			"minecraft": "1.8",
			"events": {
				"login": "say A player joined.;tell @$ Welcome!"
			}
		}
	]
}
```

We can also do `say A player joined.; tell @$ Welcome!` (with an extra space) since ServerMagic tells Java to remove leading and trailing whitespace from server commands.

Here's a more complicated example which spawns a firework where the user logs in after welcoming the user:

```JSON
{
	"global": {
		"arguments": "nogui"
	}
	"servers" [
		{
			"name": "TheLovelyServer",
			"minecraft": "1.7.2",
			"arguments": "-Xmx 4G",
			"properties": {
				"motd": "This is a lovely server.",
				"level-name": "lovely_world"
			},
			"events": {
				"start": "say Welcome to the lovely server!"
				"login": "tell @$ Welcome!; execute @$ ~ ~ ~ summon FireworksRocketEntity ~ ~ ~ {LifeTime:20,FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[65535,16777215],FadeColors:[18844]}]}}}}",
				"logout": "say Goodbye @$!"
			}
		}
	]
}
```

## Backups

ServerMagic can backup servers automatically based on how long the server has been running. There are three objects that decide how the backup system should work, all of which are under `backup`; `start`, `end` and `time`. `start` and `end` act as events when the backup starts/finishes, and support the ';' character (`start` and `end` do not yet support `@$` - if `@$` is used then it will be removed when executed.)

ServerMagic will always back up the server when it starts ~~and when it ends~~ (todo).

The `time` object specifies how many minutes will pass before ServerMagic backs up the server again. Here's an example of a server which will back up every twenty minutes:

```JSON
{
	"servers" [
		{
			"name": "BackupServer",
			"minecraft": "1.8",
			"backup": {
				"start": "say Backing up!",
				"end": "say Backup Done!",
				"time": 20
			}
		}
	]
}
```

### Combining backups, events and multiple servers:

Using everything documented in this README you can create an extensive and complicated server array in a single document:

{
	"global": {
		"arguments": ""
	}
	"servers" [
		{
			"name": "HelloWorld",
			"minecraft": "1.8.1-pre4",
			"properties": {
				"server-port": "25565",
				"motd": "HelloWorld Test Server",
				"gamemode": "1",
				"online-mode": "false",
				"level-name": "Test_World"
			},
			"events": {
				"start": "say Server started!",
				"login": "tell @$ Welcome!;execute @$ ~ ~ ~ summon FireworksRocketEntity ~ ~ ~ {LifeTime:20,FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[65535,16777215],FadeColors:[18844]}]}}}}",
				"logout": "say @$ left!"
			},
			"backup": {
				"start": "say Backing up!",
				"end": "say Backup Done!",
				"time": 20
			}
		}
		{
			"name": "GoodbyeWorld",
			"minecraft": "1.7.10",
			"properties": {
				"server-port": "25566",
				"motd": "GoodbyeWorld Test Server"
			},
			"events": {
				"start": "say started up the server",
				"login": "tp @$ 0 100 0; gamemode $0 3"
			},
			"backup": {
				"time": 60
			}
		}
	]
}

### Compiling ServerMagic

To compile ServerMagic please use the standard Java compiler with the following libraries:

* commons-io-2.4
* json-simple-1.1.1
* guava-18.0