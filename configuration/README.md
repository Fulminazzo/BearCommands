**Welcome to the official BearCommands Wiki!**

**Configuration** is a module responsible for **handling configuration files** and **user-defined objects**.
It is **independent** of the main **BearCommands**, therefore, it can be used **individually** in your project.

This guided is divided into many parts:
- in the **first** one, there is an overview for the correct way of **loading** and **using YAML configuration** files (**platform independent**);
- in the **second** one, you will learn how to properly use the [Manager type](/configuration/src/main/java/it/angrybear/managers/Manager.java), provided by **BearCommands**;
- in the **third** one, you will find a deeper look into [Savable objects](/configuration/src/main/java/it/angrybear/objects/Savable.java), a special type that **BearCommands** uses to load and save them.

| **Table of Contents**             |
|-----------------------------------|
| [Configurations](#configurations) |
| [Managers](#managers)             |
| [Objects](#objects)               |

**WARNING**: this guide assumes that you imported the [YAMLParser project](https://github.com/Fulminazzo/YAMLParser) in your project and know how to use it.
If in doubt, please check the [documentation page](https://github.com/Fulminazzo/YAMLParser).

# Configurations
To start working with configuration files, you first need to implement the [IBearConfigPlugin interface](/configuration/src/main/java/it/angrybear/interfaces/IBearConfigPlugin.java).
This interface allows to **automatically load YAML files** present in your **JAR**, as long as you call the ```loadAll()``` method upon **enabling the plugin**.

Here is an example implementation:
```java
public class TestPlugin implements IBearConfigPlugin {
    private final List<ConfigManager> configurations = new ArrayList<>();

    /*
        ...
     */

    @Override
    public List<ConfigManager> getAllConfigurations() {
        return configurations;
    }
}
```
As you can see, a list of [ConfigManager](#configmanager) is required. 
In the next section, we will look at what this class does, but for now just think of it as a **wrapper** for the **real YAML FileConfiguration**.

Say that your **resources** directory is structured like this:
```
resources/
    config.yml
    user-data.yml
    structure/
        blocks.yml
        locations.yml
        owners.yml
    lang/
        en.yml
        es.yml
        it.yml
```
Depending on the resource, you can access them in different ways:
- ```config.yml``` and ```user-data.yml``` are "**simple**" YAML files, meaning that they are **not wrapped** in any **folder**.
They will be loaded as a [ConfigManager](#configmanager);
you can access them by simply calling ```getConfig(String configName)``` and passing their name (without the extension) as argument.
**NOTE**: since ```config.yml``` is a special type of configuration, you can directly access it by invoking ```getConfiguration()```.
- ```blocks.yml```, ```locations.yml``` and ```owners.yml``` are "**special**" files, **wrapped** in the ```structure``` **folder**.
To access them, you will need an extra step. 
They will be loaded as a [MultiConfigManager](#multiconfigmanager);
- ```en.yml```, ```es.yml``` and ```it.yml```, as before, are "**special**" files. 
However, since their **folder** is called _lang_, they will be loaded as [LangConfigManager](#langconfigmanager).

**WARNING**:
The configuration managers are simply a wrapper for the configuration files.
As such, **before retrieving any of them**, it is required to **invoke** the ```ConfigManager#load(IBearPlugin)``` method.

| **Table of Contents**                     |
|-------------------------------------------|
| [ConfigManager](#configmanager)           |
| [MultiConfigManager](#multiconfigmanager) |
| [LangConfigManager](#langconfigmanager)   |
| [IBearConfig](#ibearconfig)               |

## ConfigManager
This is the simplest implementation of [ConfigManager](/configuration/src/main/java/it/angrybear/objects/configurations/ConfigManager.java).
A **configuration manager** is a class **responsible** for **loading** a **YAML file accordingly**.
More specifically, you can **specify**:
- **how it should be loaded** ([LoadPolicy](#loadpolicy));
- a **list of keys** to be **ignored** while **checking** (by using ```ConfigManager#addIgnoredKeys(String...)``` and ```ConfigManager#removeIgnoredKeys(String...)```).

You can access the loaded configuration using ```ConfigManager#getConfig()```.

### LoadPolicy
[LoadPolicy](/configuration/src/main/java/it/angrybear/enums/LoadPolicy.java) allows designating the **preferred method** for **loading** a file.
It supports three values:
- ```IGNORE```: loads the file and **skips every check**;
- ```WARN```: loads the file and **checks** if there are any **missing entries** or **invalid values** (wrong types). 
Then, it outputs the result using ```IBearPlugin#logWarning(String)```;
- ```WARN_AND_CORRECT```: similarly to ```WARN```, **checks and warns** every **error**.
Then, it tries to **correct these mistakes** by creating a **backup file** and **overwriting** the **main one** with default values.

By **default**, every configuration is loaded with ```WARN_AND_CORRECT``` policy.
Changing this is trivial:
- if you are working with a [ConfigManager](#configmanager), simply use ```ConfigManager#setLoadPolicy(LoadPolicy)```;
- if you are working with the [IBearConfigPlugin](#configurations), additional steps are required: 
  - before calling the ```IBearConfigPlugin#loadAll()``` method, get a **ConfigManager** using ```IBearConfigPlugin#getConfigManager(String)```.
  If it **does not exist**, it will simply **be created** and returned;
  - next, **set the load policy** as explained previously;
  - finally, you can use ```IBearConfigPlugin#loadAll()``` to **automatically load** any configuration.

You can do this process with **keys to ignore** as well.
Let's see an example to sum up:
```java
/*
   Method to enable the plugin
 */
public void enable() throws Exception {
  // Only warn when loading config.yml and 
  // ignore any "players.*" section
  ConfigManager configManager = getConfigManager("config.yml");
  configManager.setLoadPolicy(LoadPolicy.WARN);
  configManager.addIgnoredKeys("players");
  loadAll();
}
```

## MultiConfigManager
As anticipated before, a [MultiConfigManager](/configuration/src/main/java/it/angrybear/objects/configurations/MultiConfigManager.java)
is an **implementation of ConfigManager** that expects **multiple configurations**.
Let's take the previous example:
```
resources/
    structure/
        blocks.yml
        locations.yml
        owners.yml
```
The ```structure``` directory will be automatically loaded by [IBearConfigPlugin](#configurations) as a **MultiConfigManager**.
To access it from the main class, call ```IBearConfigPlugin#getConfigManager(String)``` passing the **folder name** as argument.
Then, use ```MultiConfigManager#getConfig(String)``` to retrieve the desired **config**.
Example:
```java
MultiConfigManager multiConfigManager = getConfigManager("structure");
// Access the blocks.yml file
FileConfiguration blocks = multiConfigManager.getConfig("blocks");
// Access the locations.yml file
FileConfiguration locations = multiConfigManager.getConfig("locations");
// Access the owners.yml file
FileConfiguration owners = multiConfigManager.getConfig("owners");
```
Using ```MultiConfigManager#getConfig()``` will return the **first loaded configuration** (in this case, ```blocks.yml```).  

## LangConfigManager
[LangConfigManager](/configuration/src/main/java/it/angrybear/objects/configurations/LangConfigManager.java)
is a **special version** of [MultiConfigManager](#multiconfigmanager).
This implementation only exists if the directory is called ```lang``` or ```messages```, and it is responsible for **handling languages YAML files**.
It does so by normally **loading any file** present in those **folders**, but it **keeps** only the **ones** that match the **languages specified** in the 
[Language enum](/configuration/src/main/java/it/angrybear/enum/Language.java).

So, for example, a ```test.yml``` file would be **removed**, since ```test``` is not a **valid language**.

After loading, the manager also looks for a ```en.yml``` file. 
This will be the **default configuration** returned using ```LangConfigManager#getConfig()```.
If such a file **does not exist**, it uses [MultiConfigManager#getConfig()](#multiconfigmanager).

Then, it uses **this file to compare every other file** to it.
This means that if the [load policy](#loadpolicy) is set to ```WARN``` or ```WARN_AND_CORRECT```, the **default file** will be used as **reference** for any other file.
If those have **missing** or **invalid values**, they will be respectively **reported** or **corrected**.

Let's make an example:
```
resources/
  lang/
    en.yml
    it.yml
    fu.yml
```
Where the contents are:
- ```en.yml```:
```yaml
greeting: "Hello!"
farewell: "Goodbye!"
```
- ```it.yml```:
```yaml
greeting: "Ciao!"
```
- ```fu.yml```:
```yaml
greeting: "Blabla!"
farewell: "Bliblu!"
```
Here is what happens:
- the manager normally dumps and loads all three files:
```
<current-path>/
  lang/
    en.yml
    it.yml
    fu.yml
```
- since a ```en.yml``` file exists, it is used as reference file:
  - ```it.yml``` is compared against it, and it is found that it is missing ```farewell```.
  Assuming that the [load policy](#loadpolicy) is set to ```WARN_AND_CORRECT```, the file gets corrected.
  ```it.yml```:
  ```yaml
  greeting: "Ciao!"
  # Using default value from en.yml
  farewell: "Goodbye!" 
  ```
  - ```fu.yml``` is not a valid language. Therefore, it gets deleted:
  ```
  <current-path>/
    lang/
      en.yml
      it.yml
  ```

You can now retrieve any language by simply using ```LangConfigManager#getConfig(Language)``` 
(**NOTE**: if you try to retrieve a language that is not present, the default config will be used).

## IBearConfig
Now that we covered everything that you need to know on loading YAML files into memory, it is time to start **working** with them.
A convenient way of doing that is using [IBearConfig](/configuration/src/main/java/it/angrybear/interfaces/configurations/IBearConfig.java).

On its own, this interface might not look fancy, but its implementations are able to greatly **simplify the developer's work**.

| Table of Contents                       |
|-----------------------------------------|
| [IBearConfigOption](#ibearconfigoption) |
| [IBearMessage](#ibearmessage)           |

### IBearConfigOption
This interface has been designed to be used in **combination** with a ```config.yml```, but it can work with **any configuration** you like.
The idea behind it is that you have a file with **multiple options** defined by the user, and you seek an **easy** and **fast way** to **load** and **retrieve** them.

Thanks to [IBearConfigPlugin automatic loading](#configurations), we already covered the **first part**.
For the **second**, this is where [IBearConfigOption](/configuration/src/main/java/it/angrybear/interfaces/configurations/IBearConfigOption.java) comes in.
Let's see it in action:

Start by creating an **enum** that implements the interface:
```java
public enum TestConfig implements IBearConfigOption {
    OPTION("option"),
    SECOND_OPTION("second-option")
    ;
    
    private final String path;

    TestConfig(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public IBearConfigPlugin getPlugin() {
        return TestPlugin.getInstance();
    }
}
```
As you can see, we had to override some methods:
- ```getPath()```, that should return the **corresponding YAML path**;
- ```getPlugin()```, that should return the **current working IBearConfigPlugin instance** (the one that loaded the config file).

That's it! Now you can **fetch any object** from the loaded ```config.yml``` file!
Say that it contains:
```yaml
option: 10
second-option: "Hello world!"
```
You can do:
```java
Integer option = TestConfig.OPTION.getInteger(); // Returns 10
String secondOption = TestConfig.SECOND_OPTION.getString(); // Returns "Hello world!"
```

By default, [IBearConfigOption](/configuration/src/main/java/it/angrybear/interfaces/configurations/IBearConfigOption.java)
uses ```IBearConfigPlugin#getConfig()``` to get the **main configuration** file.
However, you can simply change that by **overriding** the method ```IBearConfigOption#getConfig()```:
```java
public enum TestConfig implements IBearConfigOption {
    ;

    private final String path;

    TestConfig(String path) {
        this.path = path;
    }
    
    @Override
    public FileConfiguration getConfig() {
        return getPlugin().getConfig("another-config.yml");
    }
}
```

### IBearMessage
[IBearMessage](/configuration/src/main/java/it/angrybear/interfaces/configurations/IBearMessage.java)
is a special implementation of [IBearConfig](#ibearconfig) designed for [languages YAML files](#langconfigmanager).
It allows to easily **retrieve string** or **lists messages** from the loaded configuration and **formatting them** accordingly.

To start using it, again create an **enum**:
```java
public enum TestMessage implements IBearMessage {
    PREFIX("prefix"),
    GREETING("greeting"),
    FAREWELL("farewell"),
    ;
    
    private final String path;

    TestMessage(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public IBearConfigPlugin getPlugin() {
        return TestPlugin.getInstance();
    }
}
```
As you can see, we had to override some methods:
- ```getPath()```, that should return the **corresponding YAML path**;
- ```getPlugin()```, that should return the **current working IBearConfigPlugin instance** (the one that loaded the config file).

Now you have many options to obtain messages. Here are some examples:
```java
String message = TestMessage.GREETING.getMessage();
/*
  WARNING:
  For this to work, a PREFIX field is required 
  in the TestMessage enum.
*/
String prefixedMessage = TestMessage.GREETING.getMessage(true);
/*
  If an "it.yml" file does not exist,
  use the default one ("en.yml") to get the message.
  Will NOT return null.
*/
String translatedMessage = TestMessage.GREETING.getMessage(Language.IT);
String prefixedTranslatedMessage = TestMessage.GREETING.getMessage(true, Language.IT);

List<String> messages = TestMessage.FAREWELL.getMessages();
List<String> prefixedMessages = TestMessage.FAREWELL.getMessages(true);
List<String> translatedMessages = TestMessage.FAREWELL.getMessages(Language.IT);
List<String> prefixedTranslatedMessages = TestMessage.FAREWELL.getMessages(true, Language.IT);
```

**IBearMessage** even allows you to define **custom methods**!

Say you want to retrieve the messages as a [Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html).
All you have to do is simply invoke the ```IBearMessage#get()``` method.
Since it requires many parameters, let's check them one by one:
- ```boolean prefix```: a **boolean** to check if the **prefix** should be **prepended** to the result;
- ```Language lang```: the **language** of the **messages file**;
- ```BiFunctionException<FileConfiguration, String, O> getFunction```: the **function to retrieve the object** from the **YAML file**. 
In this case, we should use ```IConfiguration#get(String, Class<T>)``` specifying ```Set.class``` as class;
- ```Function<String, O> stringToOConverter```: a **function** to **convert a string** into the **desired output**. 
This will be used to send predefined messages in case of errors;
- ```BiFunction<O, String, O> prefixApplier```: a **function** to **apply the prefix** to the object (only if ```boolean prefix``` is ```true```);
- ```Function<O, O> messageParser```: the **final function** called to **format the object** (for example, when **coloring** it).

To sum up, our ```getSetMessages()``` method will look like this: 
```java
public Set<String> getSetMessages(boolean prefix, Language lang) {
    return get(prefix, lang,
            // Get a Set object from the language configuration file.
            (language, path) -> (Set<String>) language.get(path, Set.class),
            // Create a Set containing only one element.
            message -> new HashSet<>(Arrays.asList(message)),
            // For every element of the set, prepend the prefix.
            (set, prefixString) -> set.stream().map(s -> prefixString + s).collect(Collectors.toSet()),
            // For every element of the set, format it using an external function.
            set -> set.stream().map(s -> StringUtils.parseMessage(s)).collect(Collectors.toSet())
    );
}
```

# Managers
Many times, during your coding career, you will be required to **store** a kind of **objects** for later use, and to **provide** easy systems to **retrieve**, add or remove them.
This is where the [Manager class](/configuration/src/main/java/it/angrybear/managers/Manager.java) comes into play.
This class allows you to **manage** (as the name suggests) and **store** many **objects** across your plugin.
To better understand how this particular object works, let's see an example.

Pretend you have been tasked with creating a home plugin.
A home is simply a set of coordinates defined by the player.
Many plugins use this system to allow players to return to previously created homes (and effectively reduce travel times).

One way you could implement this is by defining a Home object:
```java
public class Home {
    private final UUID playerUUID;
    private final String name;
    private final double x;
    private final double y;
    private final double z;

    public Home(UUID playerUUID, String name, double x, double y, double z) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
```
As you can see, we are storing:
- ```playerUUID```: the unique identifier of the player;
- ```name```: the name of the home;
- ```x```: the x position;
- ```y```: the y position;
- ```z```: the z position.

Undoubtedly, creating a home by calling the constructor is not enough.
You will need to store the newly created object in some way.

Let's start by creating a **HomeManager** class that extends **Manager**:
```java
public class HomeManager extends Manager<Home> {
}
```
As you can see, **no additional configuration** or **method overriding** is **required**: **Manager works out of the box**!

Now you have access to methods such as:
- ```add(Home t)```: **add** a new home;
- ```remove(Home t)```: **remove** a home;
- ```getObjects()```: returns the **list** of **stored homes**.

However, you may want to **implement** more **specific methods** to further simplify later coding.

Let's start by creating some appropriate getter methods:
```java
/**
 * Returns a home owned by the player with the given name.
 *
 * @param playerUUID the player unique identifier
 * @param name       the name
 * @return the home (null if not found)
 */
public Home getHome(UUID playerUUID, String name) {
  for (Home home : getObjects())
    if (home.getPlayerUUID().equals(playerUUID) && home.getName().equalsIgnoreCase(name))
      return home;
  return null;
}

/**
 * Returns a list of homes owned by the player.
 *
 * @param playerUUID the player unique identifier
 * @return the homes
 */
public List<Home> getHomes(UUID playerUUID) {
  List<Home> playerHomes = new ArrayList<>();
  for (Home home : getObjects()) {
    if (home.getPlayerUUID().equals(playerUUID)) {
      playerHomes.add(home);
    }
  }
  return playerHomes;
}
```
**NOTE**: in both these methods we use ```Manager#getObjects()``` to retrieve a list of the saved objects.
However, in the ```getHome()``` method, we could use a more appropriate way provided by BearCommands:
the method ```Manager#get(TriFunction<Home, F, S, Boolean>, F, S)``` will **loop** through every **object**, and **apply** the **function** to the current element, the first argument (```f```) and the second one (```s```).
Returns the first one that returns ```true```.
```java
/**
 * Returns a home owned by the player with the given name.
 *
 * @param playerUUID the player unique identifier
 * @param name       the name
 * @return the home (null if not found)
 */
public Home getHome(UUID playerUUID, String name) {
    return get((home, u, n) -> home.getPlayerUUID().equals(playerUUID) && 
                    home.getName().equals(name), playerUUID, name);
}
```

These functions can be now used in more specific remove methods:
```java
/**
 * Removes a home owned by the player with the given name.
 * Uses {@link HomeManager#getHome(UUID, String)}.
 *
 * @param playerUUID the player unique identifier
 * @param name       the name
 */
public void remove(UUID playerUUID, String name) {
    remove(getHome(playerUUID, name));
}

/**
 * Remove all player homes.
 *
 * @param playerUUID the player unique identifier
 */
public void removeHomes(UUID playerUUID) {
    for (Home home : getHomes(playerUUID)) remove(home);
}
```
**NOTE**: **BearCommands** also provides a ```Manager#remove(TriFunction<Home, F, S, Boolean>, F, S)```, but since we already used it for the **get** method, in this case is not used.

Finally, we can also create an ```add``` method, that allows to overwrite a previously set home:
```java
/**
 * Adds a new home to the manager.
 * If the player previously owned a home with the same name,
 * it overwrites it.
 *
 * @param playerUUID the player unique identifier
 * @param name       the name
 * @param x          the x position
 * @param y          the y position
 * @param z          the z position
 */
public void add(UUID playerUUID, String name, double x, double y, double z) {
    remove(playerUUID, name);
    add(new Home(playerUUID, name, x, y, z));
}
```

In this example, we only **scratched the surface** of what is possible with the **Manager** system, but there is more to uncover.
It is highly advised to check out the [Manager class source code](/configuration/src/main/java/it/angrybear/managers/Manager.java)
that goes deeper into every method with detailed explanations.

# Objects
After checking out how [Configurations](#configurations) work in **BearCommands**, it is time to learn the main power of the project: [Savable objects](/configuration/src/main/java/it/angrybear/objects/Savable.java).

**Savable objects** are a special type that allows for **automatic loading** and **saving** of **objects** in **YAML files**.

They use the [YAMLParser library](https://github.com/Fulminazzo/YAMLParser) to handle the conversion of objects 
(**NOTE**: to load user-defined objects, you should read the proper guide in the [YAMLParser documentation](https://github.com/Fulminazzo/YAMLParser#creating-your-own-yamlparser)).

Suppose you are working with a plugin that provides custom items, and you want to **save every item on its own file**
(a very impractical implementation, but for this example will be more than enough).
To create your automatically saved item, all you have to do is just **extend** the [Savable class](/configuration/src/main/java/it/angrybear/objects/Savable.java):
```java
// Assuming the plugin main class is TestPlugin
public class CustomItem extends Savable<TestPlugin> {
    
}
```
As you can see, two constructors will be required:
- ```Savable#Savable(TestPlugin, File)```: this constructor will be called when **getting the object from its respective file**.
In this stage, **no variable should be initialized** since **Savable** will **overwrite** any set value with the **ones specified** in the **YAML file**.
If you want to **avert the saving** (and therefore **loading**) of a **variable**, use the [PreventSaving annotation](/configuration/src/main/java/it/angrybear/annotations/PreventSaving.java):
```java
public CustomItem(TestPlugin plugin, File file) {
    super(plugin, file);
}
```
- ```Savable#Savable(TestPlugin, File, String)```: this constructor will be called when **creating the object for the first time**.
The string required is the **final name** given to the **file** that will be saved in the **folder specified** as _&#60;file-name&#62;.yml_.
At this stage, you should **initialize any default variable** or any **variable of interest**.
The variables that will be declared as ```final``` or that will be ```null``` will **NOT** be saved (as well as those with the [PreventSaving annotation](/configuration/src/main/java/it/angrybear/annotations/PreventSaving.java)).
```java
public CustomItem(TestPlugin plugin, File folder, String materialName, int amount) {
    super(plugin, folder, materialName);
    this.materialName = materialName;
    this.amount = amount;
}
```

And that's all! 
You can now safely load and save **CustomItem** type objects anywhere in your project:
```java
// Saving Custom Item
CustomItem customItem = new CustomItem(this, "RUBY", 1);
customItem.save();

/*
    ...
 */

// Loading Custom item
CustomItem customItem = new CustomItem(this, new File(getDataFolder(), "RUBY.yml"));
```
However, as anticipated before, this system is **not optimal**.
Using one file for every item can be **excessive**, since it allows for many files to be created in just one folder (thus creating overhead and adding useless difficulty for the configurators).
A way to solve this problem is to use just **one file** for some **categories** of **objects**.
Thankfully, [Savable](/configuration/src/main/java/it/angrybear/objects/Savable.java)
allows for that as well:
```java
File file = new File(getDataFolder(), "custom-gems.yml");
FileConfiguration gemsConfiguration = new FileConfiguration(file);

// Saving Custom Item
ConfigurationSection rubySection = gemsConfiguration.createSection("ruby");
CustomItem customItem = new CustomItem(this, getDataFolder(), "RUBY", 1);
customItem.dump(rubySection);

gemsConfiguration.save();

/*
    ...
 */

// Loading Custom item
ConfigurationSection rubySection = gemsConfiguration.getConfigurationSection("ruby");
CustomItem customItem = new CustomItem(this, rubySection);
```
This way, you can save all the custom gems of your plugin in the ```custom-gems.yml``` file (**one file for multiple items**).
**NOTE**: for this configuration to work, we had to implement a ```CustomItem#CustomItem(TestPlugin, IConfiguration)``` constructor in the **CustomItem** class.

## SavableManager
Before reading this section, you should have a good understanding of both the [Managers](#managers) and [Savable Objects](#objects) sections.
We will look into **joining together** those two concepts to even further simplify your coding.

A [SavableManager](/configuration/src/main/java/it/angrybear/managers/SavableManager.java) 
is a special type of **Manager** that expects an object of type [Savable](/configuration/src/main/java/it/angrybear/objects/Savable.java).

This manager was created to **automatically manage** the **loading** and the **saving** of multiple **Savable** objects present in **one folder**.

As always, to start just extend the [SavableManager class](/configuration/src/main/java/it/angrybear/managers/SavableManager.java):
```java
public class CustomItemsManager extends SavableManager<CustomItem, TestPlugin> {

  public CustomItemsManager(TestPlugin plugin, File folder) {
    super(plugin, folder,
            (manager, materialName) -> new CustomItem(manager.getPlugin(), manager.getFolder(), materialName, 1),
            (manager, file) -> new CustomItem(manager.getPlugin(), file));
  }
  
}
```
This constructor might seem daunting at first, but let's analyze every parameter one by one:
- the **first** argument is the **main instance** of the **plugin**;
- the **second** argument is the **folder** where the objects will be **stored** to or **loaded** from;
- the **third** argument is a **function** that will be invoked when calling the method ```SavableManager#add(String)```.
It allows you to **create a new instance** of the object (in this case, ```CustomItem```) **from a string** (the name of the material);
- the **fourth** argument is a **function** that will be invoked when calling the method ```SavableManager#reload()```, which will **clear the list** of objects and **reload everything from disk**.
The arguments passed are the **manager** itself and the **reference file** of the type.

Of course, you could implement more methods, like a ```CustomItemsManager#add(String, int)``` to add items with their specified amount, or even create some getter and remove function;
however, those are not required: **the manager will already be able to fulfill its basic functions**. 
Just use ```SavableManager#reloadAll()``` to **load every object** from the folder and ```SavableManager#saveAll()``` to **save all items** on their respective file.