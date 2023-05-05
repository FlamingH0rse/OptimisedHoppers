package me.flaming.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.flaming.PluginMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginCommands implements TabExecutor {
    private boolean isBoolean(String value) {
        return value != null && Arrays.stream(new String[]{"true", "false", "1", "0"})
                .anyMatch(b -> b.equalsIgnoreCase(value));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        // Getting the config
        FileConfiguration config = PluginMain.getPlugin().getConfig();

        if (args[0].equalsIgnoreCase("set")) {
            if (args[1].equalsIgnoreCase("debug")) {
                if (isBoolean(args[2])) {
                    PluginMain.changeConfig("Debug", args[2]);
                } else {
                    p.sendMessage("Value must be true or false");
                }
            }

            if (args[1].equalsIgnoreCase("moredebug")) {
                if (isBoolean(args[2])) {
                    PluginMain.changeConfig("MoreDebug", args[1]);
                } else {
                    p.sendMessage("Value must be true or false");
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("optimisedhoppers")) {
            List<String> argument = new ArrayList<>();
            if (args.length == 0) {
                argument.add("set");

                return argument;
            }

            // Make the tab completion only work when the args is set
            if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                argument.add("debug");
                argument.add("moredebug");

                return argument;
            }

            // Make the tab completion only work when the args is set
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                argument.add("true");
                argument.add("false");

                return argument;
            }
        }
        return null;
    }
}
