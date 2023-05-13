package me.flaming.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import me.flaming.PluginMain;

import java.util.UUID;

public class InventoryItemMoveListener implements Listener {
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
		Hopper hopperMeta = (Hopper) block.getBlockData();
		String dir = hopperMeta.getFacing().name();

		Block target = getBlockAt(block, dir);

		if (target == null || !target.getType().equals(Material.HOPPER)) return null;

		Hopper targetHopperMeta = (Hopper) target.getBlockData();
		if (!targetHopperMeta.isEnabled()) return null;

		int space = getInvFreeAmount(((BlockInventoryHolder) target.getState()).getInventory(), itemstack);

		// If both are full, returns null
		if (space == 0) return null;

		return target;
	}

	public void executeOnTrigger(InventoryMoveItemEvent e) {
		Block sourceBlock = ((BlockInventoryHolder) e.getSource().getHolder()).getBlock();
		Block targetBlock = null;
		Block prevBlock = sourceBlock;


		// Getting the final target hopper
		while (targetBlock == null) {
			Block targetPlaceholder = getTargetHopper(prevBlock, e.getItem());

			// If no target block, it returns the previous source block itself
			if (targetPlaceholder == null) targetBlock = prevBlock;

				// Else, goes to next iteration
			else prevBlock = targetPlaceholder;
		}

		// If no item movement
		// Added to prevent debug spamming
		if (sourceBlock.equals(targetBlock)) return;

		Inventory sourceInventory = e.getSource();
		Inventory targetInventory = ((BlockInventoryHolder) targetBlock.getState()).getInventory();

		int freeSpace = getInvFreeAmount(targetInventory, e.getItem());

		ItemStack originalItem = e.getItem().clone();

		for (ItemStack item : sourceInventory.getContents()) {
			if (freeSpace == 0) return;

			if (item == null) return;

			boolean isStackable = false;

			if (originalItem.getType().equals(item.getType())) {
				if ((item.hasItemMeta() && item.getItemMeta().hasDisplayName()) && (originalItem.hasItemMeta() && originalItem.getItemMeta().hasDisplayName())) {
					String item1Name = item.getItemMeta().getDisplayName();
					String item2Name = originalItem.getItemMeta().getDisplayName();

					isStackable = item1Name.equals(item2Name);

				} else isStackable = true;
			}

			if (isStackable) {
				int removeAmount = Math.min(item.getAmount(), freeSpace);

				freeSpace -= removeAmount;

				// ItemStack to be moved to new hopper
				ItemStack movingItem = new ItemStack(item);
				movingItem.setAmount(removeAmount);

				// Removes item from source inventory
				item.setAmount(item.getAmount() - removeAmount);

				// Adds item to destination inventory
				targetInventory.addItem(movingItem);

				String log = "Added " + removeAmount + " " + movingItem.getType() + " to target hopper";
				// Write to log file
				PluginMain.logToFile(log, false);

				// Show Debug to players that has Debug enabled
				for (UUID puuid : PluginMain.debugUsers) {
					Player p = Bukkit.getPlayer(puuid);
					if (p != null) p.sendMessage(log);
				}
			}
		}


		// Show MoreDebug to players that has MoreDebug enabled
		String slog = "Source Hopper: " + sourceBlock.getX() + "," + sourceBlock.getY() + "," + sourceBlock.getZ();
		String tlog = "Target Hopper: " + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();

		// Write to log file
		PluginMain.logToFile(slog, true);
		PluginMain.logToFile(tlog, true);

		for (UUID puuid : PluginMain.moredebugUsers) {
			Player p = Bukkit.getPlayer(puuid);
			if (p != null) {
				p.sendMessage(slog);
				p.sendMessage(tlog);
			}
		}
	}

	@EventHandler
	public void onInvItemMove(InventoryMoveItemEvent e) {
		// To be updated to a separate methods, checking for all containers
		if (!e.getSource().getType().equals(InventoryType.HOPPER) && !(e.getSource().getHolder() instanceof BlockInventoryHolder))
			return;

		e.setCancelled(true);
		new BukkitRunnable() {
			public void run() {
				executeOnTrigger(e);
			}
		}.runTaskLater(PluginMain.getPlugin(), 1);
	}
}
