package it.angrybear.Utils;

import it.angrybear.Commands.ABearSubCommand;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubCommandsUtils {

    public static <S extends ABearSubCommand> S getSubCommand(List<S> subCommands, Object sender, String arg) {
        if (arg == null) return null;
        return subCommands.stream()
                .filter(s -> !s.isPlayerOnly() || ServerUtils.isPlayer(sender))
                .filter(s -> new ReflObject<>(sender).getMethodObject("hasPermission", s.getPermission()))
                .filter(s -> Stream.concat(Stream.of(s.getName()), Arrays.stream(s.getAliases())).anyMatch(a -> a.equalsIgnoreCase(arg)))
                .findAny().orElse(null);
    }

    public static <S extends ABearSubCommand> List<S> getExecutableSubCommands(List<S> subCommands, Object sender) {
        return subCommands.stream()
                .filter(s -> !s.isPlayerOnly() || ServerUtils.isPlayer(sender))
                .filter(s -> new ReflObject<>(sender).getMethodObject("hasPermission", s.getPermission()))
                .collect(Collectors.toList());
    }

    public static <S extends ABearSubCommand> List<String> getExecutableSubCommandsString(List<S> subCommands, Object sender) {
        return getExecutableSubCommands(subCommands, sender).stream()
                .flatMap(s -> Stream.concat(Stream.of(s.getName()), Arrays.stream(s.getAliases())))
                .collect(Collectors.toList());
    }
}
