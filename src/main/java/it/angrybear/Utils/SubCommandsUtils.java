package it.angrybear.Utils;

import it.angrybear.Commands.BearSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubCommandsUtils {

    public static <S extends BearSubCommand<?>> S getSubCommand(List<S> subCommands, CommandSender sender, String arg) {
        if (arg == null) return null;
        return subCommands.stream()
                .filter(s -> !s.isPlayerOnly() || sender instanceof Player)
                .filter(s -> sender.hasPermission(s.getPermission()))
                .filter(s -> Stream.concat(Stream.of(s.getName()), Arrays.stream(s.getAliases())).anyMatch(a -> a.equalsIgnoreCase(arg)))
                .findAny().orElse(null);
    }

    public static <S extends BearSubCommand<?>> List<S> getExecutableSubCommands(List<S> subCommands, CommandSender sender) {
        return subCommands.stream()
                .filter(s -> !s.isPlayerOnly() || sender instanceof Player)
                .filter(s -> sender.hasPermission(s.getPermission()))
                .collect(Collectors.toList());
    }

    public static <S extends BearSubCommand<?>> List<String> getExecutableSubCommandsString(List<S> subCommands, CommandSender sender) {
        return getExecutableSubCommands(subCommands, sender).stream()
                .flatMap(s -> Stream.concat(Stream.of(s.getName()), Arrays.stream(s.getAliases())))
                .collect(Collectors.toList());
    }
}
