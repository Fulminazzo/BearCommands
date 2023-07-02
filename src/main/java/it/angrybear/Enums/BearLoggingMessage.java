package it.angrybear.Enums;

public class BearLoggingMessage extends ClassEnum {
    public static final BearLoggingMessage GENERAL_ERROR_OCCURRED = new BearLoggingMessage("An error occurred while %task%: %error%");
    public static final BearLoggingMessage GENERAL_CANNOT_BE_NULL = new BearLoggingMessage("%object% cannot be null");

    public static final BearLoggingMessage PARSE_ITEM_ERROR = new BearLoggingMessage("There was an error parsing item \"%item%\".");
    public static final BearLoggingMessage FILE_CREATE_ERROR = new BearLoggingMessage("There was an error while creating file \"%file%\"!");
    public static final BearLoggingMessage FOLDER_CREATE_ERROR = new BearLoggingMessage("There was an error while creating folder \"%folder%\"!");
    public static final BearLoggingMessage FILE_RENAME_ERROR = new BearLoggingMessage("There was an error while renaming file \"%file%\"!");
    public static final BearLoggingMessage FILE_DELETE_ERROR = new BearLoggingMessage("There was an error while deleting file \"%file%\"!");
    public static final BearLoggingMessage FOLDER_DELETE_ERROR = new BearLoggingMessage("There was an error while deleting folder \"%folder%\"!");
    public static final BearLoggingMessage SAVE_CONFIG_ERROR = new BearLoggingMessage("There was an error while saving config \"%config%\" in file \"%file%\": %error%");
    public static final BearLoggingMessage MESSAGE_ERROR = new BearLoggingMessage("&4Message not found.");
    public static final BearLoggingMessage CONFIG_ERROR = new BearLoggingMessage("Some errors where found while reading config file %config%.");

    public static final BearLoggingMessage INVALID_TYPE = new BearLoggingMessage("Invalid type in %entry%: expected %expected% but got %received%");
    public static final BearLoggingMessage MISSING_ENTRIES = new BearLoggingMessage("Missing entries:");
    public static final BearLoggingMessage INVALID_ENTRIES = new BearLoggingMessage("Invalid entries:");
    public static final BearLoggingMessage AUTO_CORRECT = new BearLoggingMessage("The plugin will try to correct these mistakes.");

    public static final BearLoggingMessage FIELD_NOT_FOUND = new BearLoggingMessage("Field \"%field%\" not found in %object%");
    public static final BearLoggingMessage EMPTY_CONSTRUCTOR_NOT_FOUND = new BearLoggingMessage("Constructor with no arguments was not found in class \"%class%\"");

    public static final BearLoggingMessage ENABLING = new BearLoggingMessage("&fEnabling &6%plugin-name% &fv&b%plugin-version%&f. Reloading all dependent plugins.");
    public static final BearLoggingMessage DISABLING = new BearLoggingMessage("&fDisabling &6%plugin-name% &fv&b%plugin-version%&f. &cWARNING! Dependent plugins may stop working.");

    public static final BearLoggingMessage REGISTERED_PLACEHOLDERS = new BearLoggingMessage("%plugin-name% v%plugin-version% found. Registering placeholders.");
    public static final BearLoggingMessage PLACEHOLDER_API_REQUIRED = new BearLoggingMessage("This plugin requires PlaceholderAPI but it was not found. Please install it in your plugins folder and try again");
    public static final BearLoggingMessage PLACEHOLDER_API_NOT_FOUND = new BearLoggingMessage("PlaceholderAPI not found. Placeholders won't be available.");
    public static final BearLoggingMessage DISABLING_CONFLICT_PLUGIN = new BearLoggingMessage("Disabling plugin %plugin-name% because it already loaded instances of BearCommands.");

    public static final BearLoggingMessage RELOAD_UNSUPPORTED = new BearLoggingMessage("WARNING: You are probably reloading %plugin%. This is NOT supported by this plugin and it is unadvised. It is suggested to restart the server or reload the BearCommands Library.");

    private final String message;

    public BearLoggingMessage(String message) {
        this.message = message;
    }

    public String getMessage(String... strings) {
        String tmp = message;
        if (strings.length > 1)
            for (int i = 0; i < strings.length; i += 2)
                if (strings[i] != null)
                    tmp = tmp.replace(strings[i], strings[i + 1] == null ? "null" : strings[i + 1]);
        return tmp;
    }
}