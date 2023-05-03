package me.flaming;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("OptimisedHoppers is enabled!");
	}

	@Override
	public void onDisable() {
		getLogger().info("OptimisedHoppers has been disabled!");
	}
}
