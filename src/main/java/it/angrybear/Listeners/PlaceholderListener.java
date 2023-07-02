package it.angrybear.Listeners;

import it.angrybear.BearPlugin;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Objects.Placeholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderListener extends PlaceholderExpansion {
    private final BearPlugin<?, ?> plugin;

    public PlaceholderListener(BearPlugin<?, ?> plugin) throws Exception {
        if (plugin == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "Plugin"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String result = null;
        for (Placeholder subPlaceholder : plugin.getPlaceholders()) {
            result = subPlaceholder.parsePlaceholder(player, identifier);
            if (result != null) break;
        }
        return result;
    }
}
