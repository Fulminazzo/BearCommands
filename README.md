# BearCommands
BearCommands is a Minecraft Spigot Library designed to simplify the use of NMS, the creation of commands and subcommands, saving custom classes and more. 
In this page you will find everything related to this plugin and a clarification on how to use the different methods provided in order to work with it.
<br>
Since version 6.0, the library is now compatible with BungeeCord, so when in this guide you will find "both platforms" it is intended to be "both Bukkit and BungeeCord".

| Table of Contents           |
|-----------------------------|
| [Annotations](#Annotations) |
| [Enums](#Enums)             |
| [Exceptions](#Exceptions)    |
| [Managers](#Managers)     |
| [Objects](#Objects)     |
| [Utilities](#Utilities)     |

## Annotations
There is currently only one Annotation ```PreventSaving```: used to prevent saving of a Field in a Class that extends [Savable](#Savable).

## MessagingCommand
The Messaging Commands are a custom way to handle the BungeeCord Plugin Messaging messages.
By extending this class you can create a custom command that will be loaded by [BearPlugin](#BearPlugin) or [BungeeBearPlugin](#BungeeBearPlugin) and executed when a plugin message, that has the subchannel as the command name, will be sent.

## Enums
Enums is a list of classes designed to help you with the creation of static variables in your plugin.
In here you will find "Enums" such as [BearConfigOption](#BearConfigOption) and [BearPermission](#BearPermission). Read below to know more.

| Enums                                     |
|-------------------------------------------|
| [BearConfigOption](#BearConfigOption)     |
| [BearLoggingMessage](#BearLoggingMessage) |
| [BearPermission](#BearPermission)         |

### BearConfigOption
A custom enum class with many methods to get objects from the plugin <i>config.yml</i> such as ```getString()```, ```getIntegerList()```, ```getMaterial()``` (only in bukkit) and more.
To work with it, you can define your own ConfigOption class and extend the BearCommands one.

### BearLoggingMessage
A list of static messages displayed in console throughout the plugin. It is helpful to display internal errors and more.
You can also create your owns by extending the class.

### BearPermission
Class required to work with [BungeeBearCommand](#BungeeBearCommand), [BearCommand](#BearCommand), [SubBearCommand](#SubBearCommand) and more.
It is used to define custom Permissions. You can automatically load them by using the method ```setPermissionsClass``` found in [BearPlugin](#BearPlugin).

## Exceptions

| Exceptions              |
|-------------------------|
| ExpectedClassException  |
| ExpectedPlayerException |
| PluginException         |
| YamlElementException    |

## Managers

| Managers                                            |
|-----------------------------------------------------|
| [BearPlayerManager](#BearPlayerManager)             |
| [SimpleBearPlayerManager](#SimpleBearPlayerManager) |

### BearPlayerManager
The default player manager that handles addition and removal of players in game. This class is platform independent and designed to work with objects that are instance of [BearPlayer](#BearPlayer) or [BungeeBearPlayer](#BungeeBearPlayer).
You can use it by invoking the ```setPlayersManagerClass``` for online players or ```setOfflinePlayersManagerClass``` (only for Bukkit) found in [BearPlugin](#BearPlugin) or [BungeeBearPlugin](#BungeeBearPlugin).

### SimpleBearPlayerManager
A copy of BearPlayerManager already set to prevent saving of players.

## Objects
The library offers several objects in order to facilitate your work in creation of custom classes and more.
The following is a list of the general objects available for both platforms.

| Objects                                   |
|-------------------------------------------|
| [Configuration](#Configuration)           |
| [ConfigurationCheck](#ConfigurationCheck) |
| [InfiniteTimer](#InfiniteTimer)           |
| [Timer](#Timer)                           |
| [YamlElements](#YamlElements)             |
| [Printable](#Printable)                   |
| [Savable](#Savable)                       |
| [UtilPlayer](#UtilPlayer)                 |
| [YamlPair](#YamlPair)                     |

### Configuration
A wrapper for FileConfiguration (Bukkit) and Configuration (BungeeCord).

### ConfigurationCheck
This class takes as input a Configuration and confronts it with its corresponding jar resource. If it finds differences or errors, it saves them in lists.
It is used in [ConfigUtils](#ConfigUtils) when checking and loading a config.

### InfiniteTimer
A recreation of [Timer](#Timer) but with infinite duration.

### Timer
A simple cross-platform timer that executes tasks every given milliseconds and when it stops executes a given function.

### YamlElements
YamlElements is a package with many YamlObjects. A YamlObject is a wrapper for an Object that allows to save it into a [Configuration](#Configuration) Section and load it from there.
This class is used in [Savable](#Savable) when dumping or loading an object and its main purpose is to automate this tasks for the user, so that she/he can only focus on the use of the class fields.
Here' a list of the general YamlElements, but you should also check the [Bukkit](#Bukkit) ones.

| YamlElements                                                                       |
|------------------------------------------------------------------------------------|
| ArrayYamlObject                                                                    |
| BearPlayerYamlObject (compatible with [BungeeBearPlayer](#BungeeBearPlayer))       |
| CollectionYamlObject (suggested against ListYamlObject)                            |
| DateYamlObject                                                                     |
| EnumYamlObject                                                                     |
| GeneralYamlObject (fallback class for not given types; only works if serializable) |
| IterableYamlObject (abstract class for List, Map and Collection YamlObjects)       |
| ListYamlObject                                                                     |
| MapYamlObject                                                                      |
| UUIDYamlObject                                                                     |

### Printable
An abstract class that provides a ```toString()``` method that automatically prints out the object fields.

### Savable
An abstract class that provides methods to automatically load and save objects from and into a YAML file.

### UtilPlayer
A wrapper for the Player class, compatible with both platforms and used for [BearPlayer](#BearPlayer) and [BungeeBearPlayer](#BungeeBearPlayer) creation.

### YamlPair
A class that associates to a class its respective YamlObject class. 
You can specify custom YamlPairs before enabling your plugin by using the method ```addAdditionalYamlPairs()``` in [BearPlugin](#BearPlugin) or [BungeeBearPlugin](#BungeeBearPlugin).
These will be saved and used when saving [Savable](#Savable) objects.

## Utilities
There are several utilities designed to help you in many tasks.

| Utilities                                 |
|-------------------------------------------|
| [ConfigUtils](#ConfigUtils)               |
| [FileUtils](#FileUtils)                   |
| [HexUtils](#HexUtils)                     |
| [JarUtils](#JarUtils)                     |
| [MessagingUtils](#MessagingUtils)         |
| [NumberUtils](#)                          |
| [SerializeUtils](#SerializeUtils)         |
| [ServerUtils](#ServerUtils)               |
| [StringUtils](#StringUtils)               |
| [SubCommandsUtils](#)                     |
| [TextComponentUtils](#TextComponentUtils) |
| [TimeUtils](#TimeUtils)                   |
| [TitleUtils](#TitleUtils)                 |
| [VersionsUtils](#VersionsUtils)           |

### ConfigUtils
A group of functions to work with FileConfigurations. By using the class [Configuration](#Configuration), the class becomes platform independent, meaning that you can use these methods both with Bukkit and BungeeCord plugins.

### FileUtils
A group of functions to work with files creations, deletion and modification.

### HexUtils
This class only offers methods that work in Minecraft 1.16 and above and it is designed to translate a text with hex colors into colored in game messages.

### JarUtils
A group of functions to work with jar files and read their contents.

### MessagingUtils
An utility class to work with BungeeCord Plugin Messaging channels. It only contains the method ```sendPluginMessage```;

### SerializeUtils
A group of functions to serialize a serializable object in Base64 and vice-versa. It also offers a way to serialize UUIDs.

### ServerUtils
A class designed to be platform independent that offers many methods such as ```getBukkit()```, ```getProxyServerInstance()```, ```isBukkit()``` and more.

### StringUtils
A group of functions designed to help work with strings. Among these you will find replace methods that maintain the previous color ("&dhey friend".replace("friend", "buddy") will become "&dhey buddy&d"), a way to automatically print the fields of an object in a String, a capitalize method and more.

### TextComponentUtils
This class is designed to work with both Bukkit and BungeeCord platforms and it allows to create Text HoverEvents based on the current version.

### TimeUtils
A group of functions to help work with time. In particular, the method ```parseTime()``` allows to convert a time (in long) into a String given certain parameters (years, months, days...).

### TitleUtils
A group of function that helps the creation of titles and subtitles in both platforms.

### VersionsUtils
A way to check the specific version you are currently working on.