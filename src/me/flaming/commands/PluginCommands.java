package me.flaming.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.flaming.PluginMain;

import java.util.ArrayList;
import java.util.List;

public class PluginCommands implements TabExecutor {
    private String arrayOrDefaultValue(String[] args, int index, String defaultValue) {
        if (args.length <= index) return defaultValue;
        return args[index];
    }
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); //match a number with optional '-' and decimal.
    }
    private String colorStr(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private void aboutMessage(Player p) {
        BaseComponent[] repoButton = new ComponentBuilder("[Click Here]\n").bold(true).color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/FlamingH0rse/OptimisedHoppers"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Visit the repo"))).create();

        BaseComponent[] helpButton = new ComponentBuilder("[Click Here]\n").bold(true).color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/optimisedhoppers help"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("View Help"))).create();

        BaseComponent[] messageComponent = new ComponentBuilder("\n")
                .append("OptimisedHoppers\n").color(net.md_5.bungee.api.ChatColor.AQUA).bold(true)
                .append(" \n")
                .append("An open-source plugin that aims at optimising\n").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false)
                .append("item movements by hoppers on Minecraft\n").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false)
                .append("servers\n").color(net.md_5.bungee.api.ChatColor.GOLD).bold(false)
                .append(" \n")
                .append("Repository: ").color(net.md_5.bungee.api.ChatColor.BLUE).bold(true).append(repoButton)
                .append("Commands: ").event((ClickEvent) null).event((HoverEvent) null)
                .color(net.md_5.bungee.api.ChatColor.BLUE).bold(true).append(helpButton)
                .append("").create();

        p.spigot().sendMessage(messageComponent);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        if (args.length == 0) {
            aboutMessage(p);
        } else {
           if (args[0].equalsIgnoreCase("help")) {
               BaseComponent[] helpMessage = new ComponentBuilder("\n")
                       .append("OptimisedHoppers ").color(net.md_5.bungee.api.ChatColor.AQUA).bold(true)
                       .append("[Commands]\n\n").color(net.md_5.bungee.api.ChatColor.DARK_RED)
                       .append("/optimisedhoppers help").underlined(true).color(net.md_5.bungee.api.ChatColor.GOLD).bold(false)
                       .append(" - Shows this message\n").underlined(false).color(net.md_5.bungee.api.ChatColor.WHITE)
                       .append("/optimisedhoppers about - ").underlined(true).color(net.md_5.bungee.api.ChatColor.GOLD)
                       .append(" - View plugin's about\n").underlined(false).color(net.md_5.bungee.api.ChatColor.WHITE)
                       .append("/optmisedhoppers toggle [debug/moredebug]").underlined(true).color(net.md_5.bungee.api.ChatColor.GOLD)
                       .append(" - Shows update of debug/moredebug\n").underlined(false).color(net.md_5.bungee.api.ChatColor.WHITE)
                       .append("/optimisedhoppers check [debug/moredebug]").underlined(true).color(net.md_5.bungee.api.ChatColor.GOLD)
                       .append(" - Check debug/moredebug history\n").underlined(false).color(net.md_5.bungee.api.ChatColor.WHITE).create();

               p.spigot().sendMessage(helpMessage);

           } else if (args[0].equalsIgnoreCase("about")) {
               aboutMessage(p);
           } else if (args.length >= 2) {
                // Place commands here that has more than 2 args
                if (args[0].equalsIgnoreCase("check")) {
                    if (args[1].equalsIgnoreCase("debug")) {
                        // The current page (5 is set to be the max page)
                        String userinput = arrayOrDefaultValue(args, 2, "1");

                        if (isNumeric(userinput)) {
                            int pagenumber = Integer.parseInt(userinput);

                            if (pagenumber <= 5 && pagenumber > 0) {
                                // How many stuffs will there be in 1 page
                                int content_num = 6;

                                BaseComponent[] nextButton = new ComponentBuilder("NEXT").color(net.md_5.bungee.api.ChatColor.GREEN)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/optimisedhoppers check debug " + (pagenumber + 1)))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Next Page"))).create();

                                BaseComponent[] prevButton = new ComponentBuilder("PREV").color(net.md_5.bungee.api.ChatColor.GREEN)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/optimisedhoppers check debug " + (pagenumber - 1)))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Previous Page"))).create();

                                // Spacebar
                                BaseComponent[] emptyComponent = new ComponentBuilder(" ").create();

                                BaseComponent[] messageComponent = new ComponentBuilder("Logs: ")
                                        .bold(true).color(net.md_5.bungee.api.ChatColor.GOLD)
                                        .append("[Debug]\n").bold(false).color(net.md_5.bungee.api.ChatColor.DARK_RED)
                                        .append(PluginMain.readLogs(content_num, pagenumber, false)).color(net.md_5.bungee.api.ChatColor.WHITE)
                                        .append("\n").append("\n")
                                        .append(pagenumber != 1 ? prevButton : emptyComponent)
                                        .append("・").color(net.md_5.bungee.api.ChatColor.WHITE).event((ClickEvent) null).event((HoverEvent) null)
                                        .append(pagenumber != 5 ? nextButton : emptyComponent)
                                        .append(" Page " + pagenumber + "/5").color(net.md_5.bungee.api.ChatColor.WHITE)
                                        .event((ClickEvent) null).event((HoverEvent) null).create();

                                p.spigot().sendMessage(messageComponent);
                            } else {
                                p.sendMessage(ChatColor.RED + "Value must be an integer between 1-5");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Value must be an integer between 1-5");
                        }

                    } else if (args[1].equalsIgnoreCase("moredebug")) {
                        // The current page (5 is set to be the max page)
                        String userinput = arrayOrDefaultValue(args, 2, "1");

                        if (isNumeric(userinput)) {
                            int pagenumber = Integer.parseInt(userinput);

                            if (pagenumber <= 5 && pagenumber > 0) {
                                // How many stuffs will there be in 1 page
                                int content_num = 6;

                                BaseComponent[] nextButton = new ComponentBuilder("NEXT").color(net.md_5.bungee.api.ChatColor.GREEN)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/optimisedhoppers check moredebug " + (pagenumber + 1)))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new Text("Next Page"))).create();

                                BaseComponent[] prevButton = new ComponentBuilder("PREV").color(net.md_5.bungee.api.ChatColor.GREEN)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/optimisedhoppers check moredebug " + (pagenumber - 1)))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Previous Page"))).create();

                                // U+2800 is inside the emptyComponent. It is not simply spacebar
                                BaseComponent[] emptyComponent = new ComponentBuilder("⠀").create();

                                BaseComponent[] messageComponent = new ComponentBuilder("Logs: ")
                                        .bold(true).color(net.md_5.bungee.api.ChatColor.GOLD)
                                        .append("[MoreDebug]\n").bold(false).color(net.md_5.bungee.api.ChatColor.DARK_RED)
                                        .append(PluginMain.readLogs(content_num, pagenumber, true)).color(net.md_5.bungee.api.ChatColor.WHITE)
                                        .append("\n").append("\n")
                                        .append(pagenumber != 1 ? prevButton : emptyComponent)
                                        .append("・").color(net.md_5.bungee.api.ChatColor.WHITE).event((ClickEvent) null).event((HoverEvent) null)
                                        .append(pagenumber != 5 ? nextButton : emptyComponent)
                                        .append(" Page " + pagenumber + "/5").color(net.md_5.bungee.api.ChatColor.WHITE)
                                        .event((ClickEvent) null).event((HoverEvent) null).create();

                                p.spigot().sendMessage(messageComponent);
                            } else {
                                p.sendMessage(ChatColor.RED + "Value must be an integer between 1-5");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Value must be an integer between 1-5");
                        }

                    } else p.sendMessage(ChatColor.RED + "Usage: /optimisedhoppers check [debug/moredebug]");

                } else if (args[0].equalsIgnoreCase("toggle")) {
                    if (args[1].equalsIgnoreCase("debug")) {
                        // User already has Debug toggled
                        if (PluginMain.debugUsers.contains(p.getUniqueId())) {
                            PluginMain.debugUsers.remove(p.getUniqueId());
                            p.sendMessage(colorStr("&4&lDISABLED &r&6Debug"));
                        } else {
                            PluginMain.debugUsers.add(p.getUniqueId());
                            p.sendMessage(colorStr("&a&lENABLED &r&6Debug"));
                        }

                    } else if (args[1].equalsIgnoreCase("moredebug")) {
                        // User already has MoreDebug toggled
                        if (PluginMain.moredebugUsers.contains(p.getUniqueId())) {
                            PluginMain.moredebugUsers.remove(p.getUniqueId());
                            p.sendMessage(colorStr("&4&lDISABLED &r&6More Debug"));

                        } else {
                            PluginMain.moredebugUsers.add(p.getUniqueId());
                            p.sendMessage(colorStr("&a&lENABLED &r&6MoreDebug"));
                        }

                    } else p.sendMessage(ChatColor.RED + "Usage: /optimisedhoppers toggle [debug/moredebug]");

                } else {
                    p.sendMessage(ChatColor.RED + "Unknown Command");
                }

            } else if (args[0].equalsIgnoreCase("toggle")) {
                p.sendMessage(ChatColor.RED + "Usage: /optimisedhoppers toggle [debug/moredebug]");
            } else if (args[0].equalsIgnoreCase("check")) {
                p.sendMessage(ChatColor.RED + "Usage: /optimisedhoppers check [debug/moredebug]");
            } else aboutMessage(p);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("optimisedhoppers")) {
            if (args.length == 1) {
                arguments.add("check");
                arguments.add("toggle");
                arguments.add("help");
                arguments.add("about");
            }

            // Make the tab completion only work when the args is set
            if (args.length == 2 && (args[0].equalsIgnoreCase("check") || ( args[0].equalsIgnoreCase("toggle")))) {
                arguments.add("debug");
                arguments.add("moredebug");
            }
        }
        return arguments;
    }
}
