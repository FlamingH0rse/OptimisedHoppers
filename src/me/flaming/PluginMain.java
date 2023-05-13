package me.flaming;

import me.flaming.events.InventoryItemMoveListener;
import me.flaming.commands.PluginCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

	private static PluginMain plugin;

	@Override
	public void onEnable() {
		plugin = this;
		getLogger().info("OptimisedHoppers is enabled!");
		getServer().getPluginManager().registerEvents(new InventoryItemMoveListener(), this);
		getCommand("optimisedhoppers").setExecutor(new PluginCommands());
		saveDefaultConfig();
	}

	@Override
	public void onDisable() {
		getLogger().info("OptimisedHoppers has been disabled!");
	}

	public static PluginMain getPlugin() {
		return plugin;
	}

	public static void changeConfig(String name, Boolean input) {
		getPlugin().getConfig().set(name, input);
		getPlugin().saveConfig();
		getPlugin().reloadConfig();
	}
}
