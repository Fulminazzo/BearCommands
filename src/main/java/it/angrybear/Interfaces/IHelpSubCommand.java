package it.angrybear.Interfaces;

import it.angrybear.Commands.ABearSubCommand;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unchecked")
public interface IHelpSubCommand {

    default void execute(CommandSenderWrapper sender, ISubCommandable cmd, String[] args,
                         String[] topMessages, String[] subMessages,
                         String noPermission, String subCommandNotFound, String helpMessage) {
        List<ABearSubCommand> subCommands = getObject(cmd, "getExecutableSubCommands", (Object) sender.getCommandSender());
        if (subCommands == null) return;
        if (args.length > 0) subCommands.removeIf(s -> Arrays.stream(args).noneMatch(a ->
                s.getName().equalsIgnoreCase(a) || Arrays.stream(s.getAliases()).anyMatch(a2 -> a2.equalsIgnoreCase(a))));
        if (subCommands.isEmpty()) {
            if (args.length == 0) sender.sendMessage(noPermission);
            else sender.sendMessage(subCommandNotFound.replace("%subcommand%", args[0]));
        } else {
            if (topMessages != null) Arrays.stream(topMessages).forEach(sender::sendMessage);
            subCommands.stream().sorted(Comparator.comparing(ABearSubCommand::getName)).forEach(s -> {
                ReflObject<ABearSubCommand> reflS = new ReflObject<>(s);
                String commandName = reflS.callMethod("getCommand").getMethodObject("getName");
                int size = reflS.callMethod("getInternalSubCommands").getMethodObject("size");
                sender.sendMessage(helpMessage
                        .replace("%name%", s.getName())
                        .replace("%aliases%", Arrays.toString(s.getAliases()))
                        .replace("%permission%", s.getPermission())
                        .replace("%min-arguments%", String.valueOf(s.getMinArguments()))
                        .replace("%help%", s.getDescription())
                        .replace("%usage%", s.getUsage())
                        .replace("%command%", commandName)
                        .replace("%subcommands%", String.valueOf(size))
                );
            });
            if (subMessages != null) Arrays.stream(subMessages).forEach(sender::sendMessage);
        }
    }

    default List<String> onTabComplete(CommandSenderWrapper sender, ISubCommandable cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length > 0) {
            List<ABearSubCommand> givenSubCommands = new ArrayList<>();
            for (int i = 0; i < args.length - 1; i++) {
                ABearSubCommand subCommand = getObject(cmd, "getSubCommand", sender.getCommandSender(), args[i]);
                if (subCommand != null) givenSubCommands.add(subCommand);
            }
            List<String> subCommandsStrings = getObject(cmd, "getExecutableSubCommandsStrings", (Object) sender.getCommandSender());
            if (subCommandsStrings != null) {
                subCommandsStrings.removeIf(s -> givenSubCommands.stream().anyMatch(c -> c.getName().equalsIgnoreCase(s) ||
                        Arrays.stream(c.getAliases()).anyMatch(a -> a.equalsIgnoreCase(s))));
                list.addAll(subCommandsStrings);
            }
        }
        return list;
    }

    private <O> O getObject(Object object, String methodName, Object... objects) {
        Method method = Arrays.stream(object.getClass().getMethods())
                .filter(m -> m.getName().equalsIgnoreCase(methodName))
                .findFirst().orElse(null);
        if (method == null) return null;
        method.setAccessible(true);
        try {
            return (O) method.invoke(object, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}