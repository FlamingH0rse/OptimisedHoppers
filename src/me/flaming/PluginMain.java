package me.flaming;

import me.flaming.events.InventoryItemMoveListener;
import me.flaming.util.ReverseLineInputStream;
import me.flaming.commands.PluginCommands;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class PluginMain extends JavaPlugin {
    public static List<UUID> debugUsers = new ArrayList<UUID>();
    public static List<UUID> moredebugUsers = new ArrayList<UUID>();

	private static PluginMain plugin;

	@Override
	public void onEnable() {
		plugin = this;

		checkAndCreateFileNoReturn(false);
		checkAndCreateFileNoReturn(true);

		getLogger().info("OptimisedHoppers is enabled!");
		getServer().getPluginManager().registerEvents(new InventoryItemMoveListener(), this);
		getCommand("optimisedhoppers").setExecutor(new PluginCommands());
	}

	@Override
	public void onDisable() {
		getLogger().info("OptimisedHoppers has been disabled!");
	}

	public static PluginMain getPlugin() { return plugin; }

	public static File checkAndCreateFile(boolean more) {
		try {
			File dataFolder = getPlugin().getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdir();

			File saveTo = new File(getPlugin().getDataFolder(), more ? "moredebug_logs.txt" : "debug_logs.txt");
			if (!saveTo.exists()) saveTo.createNewFile();

			return saveTo;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void checkAndCreateFileNoReturn(boolean more) {
		try {
			File dataFolder = getPlugin().getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdir();

			File saveTo = new File(getPlugin().getDataFolder(), more ? "moredebug_logs.txt" : "debug_logs.txt");
			if (!saveTo.exists()) saveTo.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void logToFile(String message, boolean more) {
		try {
			File saveTo = checkAndCreateFile(more);

			FileWriter fw = new FileWriter(saveTo, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(message);
			pw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readLogs(int remaining, int itemlist, boolean more) {
		// Remaining is the remaining skips of iteration
		itemlist--;
		int current_iteration = 0;

		// it was pagenumber - remaining before
		int required_iteration =  remaining * itemlist;
		getPlugin().getLogger().info(String.valueOf(required_iteration));

		StringBuilder result = new StringBuilder();
		File logfile = new File(getPlugin().getDataFolder(), more ? "moredebug_logs.txt" : "debug_logs.txt");

		try {
			BufferedReader logreaderv2 = new BufferedReader (new InputStreamReader (new ReverseLineInputStream(logfile)));

			while (true) {
				String line = logreaderv2.readLine();

				getPlugin().getLogger().info(line);

				if (line == null) break;

				if (required_iteration != current_iteration) {
					current_iteration++;
					continue;
				}

				result.append(line).append("\n");
				remaining--;
				if (remaining == 0) break;
			}

			while (remaining != 0) {
				result.append(remaining == 6 ? "No Results Found\n" : "\n");
				remaining--;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}
