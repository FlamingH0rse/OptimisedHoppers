package me.flaming.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.flaming.PluginMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginCommands implements TabExecutor {
    private boolean isBoolean(String value) {
        return value != null && Arrays.stream(new String[]{"true", "false"})
                .anyMatch(b -> b.equalsIgnoreCase(value));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        if (args[0].equalsIgnoreCase("set")) {
            if (args[1].equalsIgnoreCase("debug")) {
                if (isBoolean(args[2])) {

                    PluginMain.changeConfig("Debug", Boolean.valueOf(args[2]));
					p.sendMessage(ChatColor.GREEN + "Debug has been set to " + Boolean.valueOf(args[2]));

				} else p.sendMessage( ChatColor.RED + "Value must be true or false");
            }

            if (args[1].equalsIgnoreCase("moredebug")) {
                if (isBoolean(args[2])) {
                    PluginMain.changeConfig("MoreDebug", Boolean.valueOf(args[2]));
					p.sendMessage(ChatColor.GREEN + "MoreDebug has been set to " + Boolean.valueOf(args[2]));
                } else {
                    p.sendMessage(ChatColor.RED + "Value must be true or false");
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("optimisedhoppers")) {
            List<String> arguments = new ArrayList<>();
            if (args.length == 1) {
                arguments.add("set");

                return arguments;
            }

            // Make the tab completion only work when the args is set
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                arguments.add("Debug");
                arguments.add("MoreDebug");

                return arguments;
            }

            // Make the tab completion only work when the args is set
            if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                arguments.add("true");
                arguments.add("false");

                return arguments;
            }
        }
        return null;
    }
}
