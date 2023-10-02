package it.angrybear.Commands;

import it.angrybear.Enums.BearPermission;
import it.angrybear.Interfaces.ISubCommandable;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

public abstract class ABearSubCommand implements ISubCommandable {
    private final String name;
    private final String permission;
    private final String description;
    private final String usage;
    private final boolean playerOnly;
    private final String[] aliases;

    private ABearSubCommand(String commandName, String name, BearPermission permission, String description, String usage, String... aliases) {
        this(commandName, name, permission, description, usage, false, aliases);
    }

    protected ABearSubCommand(String commandName, String name, BearPermission permission, String description, String usage, boolean playerOnly, String... aliases) {
        this.name = name.toLowerCase();
        this.permission = permission == null ? null : permission.getPermission();
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        this.usage = getUsageSyntax(String.format("/%s &c", commandName) + usage);
        this.playerOnly = playerOnly;
        this.aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public abstract int getMinArguments();
}