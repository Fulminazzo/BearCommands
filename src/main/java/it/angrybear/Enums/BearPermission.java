package it.angrybear.Enums;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class BearPermission extends ClassEnum {
    protected final String permission;

    public BearPermission(String permission) {
        this.permission = permission;
    }

    public abstract String getPermission();

    protected String getPermission(JavaPlugin plugin) {
        String tmp = permission;
        if (tmp.startsWith(".")) tmp = tmp.substring(1);
        return plugin == null ? null : (plugin.getName().toLowerCase() + "." + tmp);
    }

    @Override
    public String toString() {
        return name();
    }
}