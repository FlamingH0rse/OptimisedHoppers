package me.flaming;

import me.flaming.events.InventoryItemMoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

	private static PluginMain plugin;

	@Override
	public void onEnable() {
		plugin = this;
		getLogger().info("OptimisedHoppers is enabled!");
		getServer().getPluginManager().registerEvents(new InventoryItemMoveListener(), this);
	}

	@Override
	public void onDisable() {
		getLogger().info("OptimisedHoppers has been disabled!");
	}

	public static PluginMain getPlugin() {
		return plugin;
	}
}
