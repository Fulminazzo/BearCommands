# Common
Dependencies should be declared in IBearPlugin

## Dependencies
Just a list containing every dependency
On check if one dependency is not found, throw an error

# Configuration
Configuration should have:

[//]: # (IBearConfigPlugin:)

[//]: # (- ConfigManager)

[//]: # (    - LoadPolicy)

[//]: # (- MultiConfigManager)

[//]: # (IBearConfig:)

[//]: # (- IBearConfigOption)

[//]: # (- IBearMessage)

[//]: # (Managers:)

[//]: # (- Manager)
[//]: # (Objects:)

[//]: # (- Savable)
[//]: # (  - SavableManager)

# Master
Master should have:
- Players

Just the bare minimum.

[//]: # (An automatic load as expected from [Configuration]&#40;#configurations&#41;)

Automatic player system (online and offline)

**README.md should only be helpful for the developer, not the end user**

## BearPermissions <- ?
## BearMessages
And what if we create a BearLanguagePlayer? Damn cool!
Returns string, but has to be converted:
Bukkit -> String
Paper -> TextComponent
BungeeCord -> TextComponent
Velocity -> TextComponent

## Players
Create an implementation for every platform (Bukkit, BungeeCord, Velocity)

[//]: # (Do we want to separate players from config? Maybe add a middle man? )

[//]: # (Would be nice, but irrelevant since if not loaded simply use SimpleBearPlayer)

[//]: # (and leave managers as null)
Work on listeners!

[//]: # (Create an IBearOfflinePlayerPlugin that extends the IBearPlayerPlugin one.)
We will create multiple versions of BearPlugin.

[//]: # (Add question-answer system!!)

## Listeners (package)
What about common listeners?
Would be nice to find a way to declare one listener for every platform
But I think that might be hard since it would require reflections.
```java
class SimpleListener<P> extends BearListener<P> {
    SimpleListener(IBearPlugin<P> plugin) {
        super(plugin);
    }
    
    getBearPlayer(...);
    
    getWPlayer(...);
    
    getRawPlayer(...);
    
    getWConsole();
    
    getRawConsole(...);
}
```
That would be overkill, but what if I create a method for EVERY event
Then link it up to UniversalListener
That would actually work. 
Again, overkill, but work.
But for later!
One cool thing would be
```java
class PlayerListener extends BearListener<P> {
    void onJoin(WPlayer player) {
        ...
    }
    
    void onQuit(WPlayer player) {
        ...
    }
}
```
What if we want to implement offline players? 
Well, just another manager that does NOT remove players (so a PlayerListener associated)
Just it? Well yes, but also do something like we did for Managers
```java
PlayerListener getPlayersListenerClass() {
    ... // if null, return the default. 
}
```

# MessagingChannels
Honestly, these suck. Do we really want to implement them?

# Commands (package)
For commands we will need:
```java
interface IBearCommand {
    getName();
    getPermission();
    getDescription();
    getUsage();
    getAlias();
    isPlayerOnly();
    
    void execute(WCommandSender sender, String[] args);
    
    List<String> onTabComplete(WCommandSender sender, String[] args);
    
    /*
            Execute should throw:
            consoleCannotExecute
            notEnoughArguments
            notValidInteger
            notValidNatural
            notValidDecimal
            notValidBoolean
            
            Or annotations?
            Or methods?
            Like
            execute(WCommandSender, <args>...)
            and maybe parsers (FunctionException) 
            for objects? Damn. This actually could work.
            
            getNotValidInteger();
            
            @Natural
            getNotValidNatural();
     */
}
```
and
```java
interface ISubCommandable extends IBearCommand {
    getSubCommands();
    // Common methods used for tab and execute.
    parseSubCommands();
    checkSubCommands();
}
```

# Bukkit
## BossBars
## PlaceholderListener
## YAMLParsers
## Placeholders
## Utils

# TextComponentUtils? Gonna need to separate. Probably?
# TitleUtils? Gonna need to separate