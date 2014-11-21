# ServerMagic

A Java application to download, start and configure any number of Minecraft servers from a single JSON file.

## Basic Usage

To use ServerMagic just start up the complied JAR file in a terminal with something like `java -jar servermagic.jar`

If you wish to configure your servers then open up .servermagic/config.json relative to where you opened the JAR. The file should be pretty self explanatory for those familiar with both Minecraft servers and JSON, but there are a few rules:

1. Anything inside of the properties object will modify what is in server.properties, **but it will never add anything new,** only override what is already there.
2. The config must always contain the 'global' object and the 'servers' array, and at least one server.
3. Minecraft versions *should* be a string, since Mojang's versioning system includes '1.8.1-pre1' and '14w32b', which are not numerical values.

Here's a more extensive example of the ServerMagic config:

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
		{
			"name": "Survival",
			"minecraft": "1.8.1-pre1",
			"properties": {
				"view-distance": "8",
				"gamemode": "0",
				"force-gamemode": "true",
				"server-port": "25566",
				"motd": "Survival Server"
			}
		}
	]
}
```

## Events and Backups

ServerMagic now also supports automated events and backups.

Events are called under the 'events' object under a server, and are simply server commands to execute when something happens in the log.

The following events are currently supported:

* `login` - called when a player logs in.
* `logout` - called when a player logs out.

Multiple server commands can be executed, and are separated with the `;` character. When the `@$` characters are used consecutively, events that specify a player (such as someone logging out) will replace `@$`. For example, `say Hello @$` will be replaced with `say Hello Notch` if someone named Notch were to log on.

```JSON
{
	"global": {
		"arguments": ""
	}
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
				"login": "tell @$ Welcome!; execute @$ ~ ~ ~ summon FireworksRocketEntity ~ ~ ~ {LifeTime:20,FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[65535,16777215],FadeColors:[18844]}]}}}}",
				"logout": "say Goodbye @$!"
			}
		}
	]
}
```