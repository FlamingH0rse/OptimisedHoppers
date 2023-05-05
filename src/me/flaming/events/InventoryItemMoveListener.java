package me.flaming.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.configuration.file.FileConfiguration;

import me.flaming.PluginMain;

public class InventoryItemMoveListener implements Listener {
	FileConfiguration config = PluginMain.getPlugin().getConfig();

//	boolean Debug = true;
//	boolean MoreDebug = true;

	private int getInvFreeAmount(Inventory inv, ItemStack itemstack) {
		final int[] FreeSpace = {0};

		for (ItemStack i : inv.getContents()) {
			if (i == null || i.getType().equals(Material.AIR)) FreeSpace[0] += itemstack.getMaxStackSize();
			else if (itemstack.isSimilar(i)) FreeSpace[0] += i.getMaxStackSize() - i.getAmount();
		}

		return FreeSpace[0];
	}

	private Block getBlockAt(Block block, String dir) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		World world = block.getWorld();

		Block target = null;

		switch (dir) {
			case "DOWN" -> target = world.getBlockAt(x, y - 1, z);
			case "NORTH" -> target = world.getBlockAt(x, y, z - 1);
			case "SOUTH" -> target = world.getBlockAt(x, y, z + 1);
			case "EAST" -> target = world.getBlockAt(x + 1, y, z);
			case "WEST" -> target = world.getBlockAt(x - 1, y, z);
		}
		return target;
	}

	private Block getTargetHopper(Block block, ItemStack itemstack) {
		Hopper hoppermeta = (Hopper) block.getBlockData();
		String dir = hoppermeta.getFacing().name();

		Block target = getBlockAt(block, dir);

		if (!dir.equals("DOWN")) {
			if (target != null && target.getType().equals(Material.HOPPER)) {
				/*
				* The checks below use wrong logic,
				* which was based on older versions of minecraft
				* will be changed later
				*/
				// First check in the side directions
				int space = getInvFreeAmount(((BlockInventoryHolder) target.getState()).getInventory(), itemstack);

				if (config.getBoolean("MoreDebug")) Bukkit.broadcastMessage("Hopper Location: " + block.getX() + "," + block.getY() + "," + block.getZ() + "\nfacing: " + dir + "\ncan hold: " + space + " " + itemstack.getType());

				// Second check in the hopper below
				if (space == 0) dir = "DOWN";
				target = getBlockAt(block, dir);
				space = getInvFreeAmount(((BlockInventoryHolder) target.getState()).getInventory(), itemstack);

				// If both are full, returns null
				if (space == 0) return null;
			}
		}
		if (target == null || !target.getType().equals(Material.HOPPER)) return null;
		return target;
	}

	@EventHandler
	public void onInvItemMove(InventoryMoveItemEvent e) {
		if (!e.getSource().getType().equals(InventoryType.HOPPER) && !(e.getSource().getHolder() instanceof BlockInventoryHolder))
			return;
		e.setCancelled(true);
		Block sourceBlock = ((BlockInventoryHolder) e.getSource().getHolder()).getBlock();

		Block targetBlock = null;
		Block prevBlock = sourceBlock;

		while (targetBlock == null) {
			Block targetPlaceholder = getTargetHopper(prevBlock, e.getItem());

			// If no target block, it returns the previous source block itself
			if (targetPlaceholder == null) targetBlock = prevBlock;

				// Else, goes to next iteration
			else prevBlock = targetPlaceholder;
		}

		int movingItemSlot = e.getSource().first(e.getItem().getType());
		ItemStack movingItem = e.getSource().getItem(movingItemSlot);

		Bukkit.broadcastMessage(movingItem.getAmount() + "");

		// Removes item from source
		e.getSource().removeItem(movingItem);

		// Adds item to destination inventory
		// Issue: adds only 1 of the item in the inventory
		((BlockInventoryHolder) targetBlock.getState()).getInventory().addItem(movingItem);

		if (config.getBoolean("Debug")) Bukkit.broadcastMessage("Added " + e.getItem().getAmount() + " " + e.getItem().getType() + " to target hopper");
		if (config.getBoolean("Debug")) Bukkit.broadcastMessage("Source Hopper: " + sourceBlock.getX() + "," + sourceBlock.getY() + "," + sourceBlock.getZ());
		if (config.getBoolean("Debug")) Bukkit.broadcastMessage("Target Hopper: " + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ());
	}
}
