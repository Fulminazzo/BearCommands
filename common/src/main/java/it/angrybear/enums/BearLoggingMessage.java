package it.angrybear.enums;

/**
 * A class containing many logging messages
 * used in the plugin.
 */
//TODO: Rename to BearLog
public class BearLoggingMessage {
    public static final BearLoggingMessage GENERAL_ERROR_OCCURRED = new BearLoggingMessage("An error occurred while %task%: %error%");
    public static final BearLoggingMessage GENERAL_CANNOT_BE_NULL = new BearLoggingMessage("%object% cannot be null");
    public static final BearLoggingMessage NOT_SUPPORTED = new BearLoggingMessage("This function is not supported on the current platform!");

    public static final BearLoggingMessage LOAD_ERROR = new BearLoggingMessage(GENERAL_ERROR_OCCURRED,
            "%task%", "loading field \"%field%\" for object %object%");
    public static final BearLoggingMessage DUMP_ERROR = new BearLoggingMessage(GENERAL_ERROR_OCCURRED,
            "%task%", "dumping field \"%field%\" for object %object%");

    public static final BearLoggingMessage PARSE_ITEM_ERROR = new BearLoggingMessage("There was an error parsing item \"%item%\".");
    public static final BearLoggingMessage FILE_CREATE_ERROR = new BearLoggingMessage("There was an error while creating file \"%file%\"!");
    public static final BearLoggingMessage FOLDER_CREATE_ERROR = new BearLoggingMessage("There was an error while creating folder \"%folder%\"!");
    public static final BearLoggingMessage FILE_RENAME_ERROR = new BearLoggingMessage("There was an error while renaming file \"%file%\"!");
    public static final BearLoggingMessage FILE_DELETE_ERROR = new BearLoggingMessage("There was an error while deleting file \"%file%\"!");
    public static final BearLoggingMessage FOLDER_DELETE_ERROR = new BearLoggingMessage("There was an error while deleting folder \"%folder%\"!");
    public static final BearLoggingMessage MESSAGE_ERROR = new BearLoggingMessage("&4Message not found.");
    public static final BearLoggingMessage UNEXPECTED_CLASS = new BearLoggingMessage("Expected class %expected% but got %received%.");

    public static final BearLoggingMessage ENABLING = new BearLoggingMessage("&fEnabling &6%plugin-name% &fv&b%plugin-version%&f. Reloading all dependent plugins.");
    public static final BearLoggingMessage DISABLING = new BearLoggingMessage("&fDisabling &6%plugin-name% &fv&b%plugin-version%&f. &cWARNING! Dependent plugins may stop working.");

    public static final BearLoggingMessage REGISTERED_PLACEHOLDERS = new BearLoggingMessage("%plugin-name% v%plugin-version% found. Registering placeholders.");
    public static final BearLoggingMessage PLACEHOLDER_API_REQUIRED = new BearLoggingMessage("This plugin requires PlaceholderAPI but it was not found. Please install it in your plugins folder and try again");
    public static final BearLoggingMessage PLACEHOLDER_API_NOT_FOUND = new BearLoggingMessage("PlaceholderAPI not found. Placeholders won't be available.");
    public static final BearLoggingMessage DISABLING_CONFLICT_PLUGIN = new BearLoggingMessage("Disabling plugin %plugin-name% because it already loaded instances of BearCommands.");

    public static final BearLoggingMessage YAML_ERROR = new BearLoggingMessage("Error found at \"%path%%name%\" for object \"%object%\": %message%");
    public static final BearLoggingMessage CANNOT_DECIPHER_EMPTY_ARRAY = new BearLoggingMessage("Cannot parse empty array. Type conversion will result in errors");

    public static final BearLoggingMessage NO_COMMAND_PROVIDED = new BearLoggingMessage("You did not provide any command!");
    public static final BearLoggingMessage COMMAND_NOT_FOUND = new BearLoggingMessage("Command %command% not found!");
    public static final BearLoggingMessage DEPENDENCY_REQUIRED = new BearLoggingMessage("This plugin requires %plugin% to work, but none was found. Please install %plugin% before continuing.");

    private final String message;

    public BearLoggingMessage(String message) {
        this.message = message;
    }

    public BearLoggingMessage(BearLoggingMessage bearLoggingMessage, String... strings) {
        this.message = bearLoggingMessage.getMessage(strings);
    }

    /**
     * Returns a message and replaces the given strings
     * in it. For example,
     * getMessage("hello", "world")
     * will convert the string
     * "hello friend!" in "world friend!"
     *
     * @param strings the to-replace replacement pairs of strings
     * @return the final message
     */
    public String getMessage(String... strings) {
        String tmp = message;
        if (strings.length > 1)
            for (int i = 0; i < strings.length; i += 2)
                if (strings[i] != null)
                    tmp = tmp.replace(strings[i], strings[i + 1] == null ? "null" : strings[i + 1]);
        return tmp;
    }
}