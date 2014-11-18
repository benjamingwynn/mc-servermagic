# ServerMagic

A Java application to download, start and configure any number of Minecraft servers from a single JSON file.

## Usage

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