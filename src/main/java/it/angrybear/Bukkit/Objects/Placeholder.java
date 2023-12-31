package it.angrybear.Bukkit.Objects;

import it.angrybear.Utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Placeholder {
    private final String identifier;
    private final Function<Player, String> value;
    private final BiFunction<Player, String, String> subPlaceholdersHandler;

    public Placeholder(String identifier, Placeholder... subPlaceholders) {
        this(identifier, p -> null, subPlaceholders);
    }

    public Placeholder(String identifier, String value, Placeholder... subPlaceholders) {
        this(identifier, p -> value, subPlaceholders);
    }

    public Placeholder(String identifier, Function<Player, String> value, Placeholder... subPlaceholders) {
        this(identifier, value, (p, id) -> {
            String result = null;
            for (Placeholder subPlaceholder : subPlaceholders) {
                result = subPlaceholder.parsePlaceholder(p, id);
                if (result != null) break;
            }
            if (result != null) result = StringUtils.parseMessage(result);
            return result;
        });
    }

    public Placeholder(String identifier, BiFunction<Player, String, String> subPlaceholdersHandler) {
        this(identifier, p -> null, subPlaceholdersHandler);
    }

    public Placeholder(String identifier, Function<Player, String> value) {
        this(identifier, value, (p, id) -> null);
    }

    public Placeholder(String identifier, Function<Player, String> value, BiFunction<Player, String, String> subPlaceholdersHandler) {
        this.identifier = identifier == null ? null : identifier.toLowerCase();
        this.value = value;
        this.subPlaceholdersHandler = subPlaceholdersHandler;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String parsePlaceholder(Player player, String identifier) {
        if (identifier == null) return null;
        identifier = identifier.toLowerCase();
        if (!identifier.startsWith(this.identifier)) return null;
        identifier = identifier.substring(this.identifier.length());
        if (identifier.isEmpty()) {
            String result = value.apply(player);
            if (result != null) result = StringUtils.parseMessage(result);
            return result;
        }
        if (!identifier.startsWith("_")) return null;
        return subPlaceholdersHandler.apply(player, identifier.substring(1));
    }
}