package it.angrybear.Bukkit.Utils;

import it.angrybear.Interfaces.IBearPlugin;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CommandUtils {
    public static void executeBungeeCommand(Player player, String command) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(stream);
        try {
            data.writeUTF("PerformCommand");
            data.writeUTF(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //TODO: Soon to be BearCommandsBungee.
        player.sendPluginMessage(IBearPlugin.getInstance(), "staffcore:channel", stream.toByteArray());
    }

    public static void generateHelpPage(JavaPlugin plugin, Command... commands) {
        if (plugin == null) return;

        Collection<HelpTopic> allTopics = getCommandsHelpTopic(plugin);
        if (allTopics == null) allTopics = new ArrayList<>();

        removeHelpPage(plugin, commands);
        allTopics.addAll(Arrays.stream(commands).map(GenericCommandHelpTopic::new).collect(Collectors.toList()));

        HelpTopic pluginHelpTopic = generatePluginHelpTopic(plugin, allTopics);
        Bukkit.getHelpMap().addTopic(pluginHelpTopic);
    }

    public static void removeHelpPage(JavaPlugin plugin, Command... commands) {
        if (plugin == null) return;
        Collection<HelpTopic> allTopics = getCommandsHelpTopic(plugin);
        if (allTopics == null) return;

        allTopics.removeIf(h -> isCommandHelpTopic(h, commands));
        Bukkit.getHelpMap().getHelpTopics().removeIf(h -> isCommandHelpTopic(h, commands));
        if (allTopics.isEmpty()) Bukkit.getHelpMap().getHelpTopics().removeIf(h -> h.getName().equals(plugin.getName()));
    }

    private static boolean isCommandHelpTopic(HelpTopic helpTopic, Command... commands) {
        return Arrays.stream(commands).map(Command::getName).anyMatch(n -> helpTopic.getName().equals(n) || helpTopic.getName().equals("/" + n));
    }

    public static HelpTopic getPluginHelpTopic(JavaPlugin plugin) {
        if (plugin == null) return null;
        return Bukkit.getHelpMap().getHelpTopics().stream()
                .filter(h -> h.getName().equals(plugin.getName()))
                .findAny().orElse(null);
    }

    public static Collection<HelpTopic> getCommandsHelpTopic(JavaPlugin plugin) {
        return plugin == null ? null : getCommandsHelpTopic(getPluginHelpTopic(plugin));
    }

    public static Collection<HelpTopic> getCommandsHelpTopic(HelpTopic pluginHelpTopic) {
        return new ReflObject<>(pluginHelpTopic).getFieldObject("allTopics");
    }

    public static IndexHelpTopic generatePluginHelpTopic(JavaPlugin plugin, Collection<HelpTopic> commands) {
        PluginDescriptionFile description = plugin.getDescription();
        return new IndexHelpTopic(description.getName(), description.getDescription(),
                String.format("%s.%s", description.getName(), description.getName()).toLowerCase(),
                commands == null ? new ArrayList<>() : commands, description.getDescription());
    }

    public static void syncCommands() {
        // ((CraftServer) Bukkit.getServer).syncCommands();
        new ReflObject<>(Bukkit.getServer()).callMethod("syncCommands");
    }

    public static ReflObject<Collection<Command>> getBukkitCommands() {
        // return (Collection<Command>) getCommandMap().getCommands();
        return getCommandMap().callMethod("getCommands");
    }

    public static ReflObject<HashMap<String, Command>> getKnownCommands() {
        ReflObject<CommandMap> map = getCommandMap();
        // return (HashMap<String, Command>) getCommandMap().getKnownCommands();
        return new ReflObject<>(map.getObject()).callMethod("getKnownCommands");
    }

    public static ReflObject<CommandMap> getCommandMap() {
        // return (CommandMap) Bukkit.getServer().commandMap;
        return new ReflObject<>(Bukkit.getServer()).obtainField("commandMap");
    }
}