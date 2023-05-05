package me.flaming.events;

import me.flaming.PluginMain;
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
import org.bukkit.scheduler.BukkitRunnable;


public class InventoryItemMoveListener implements Listener {

	boolean Debug = false;
	boolean MoreDebug = false;

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

		if (target == null || !target.getType().equals(Material.HOPPER)) return null;

		int space = getInvFreeAmount(((BlockInventoryHolder) target.getState()).getInventory(), itemstack);

		// If both are full, returns null
		if (space == 0) return null;

		return target;
	}

	public void executeOnTrigger(InventoryMoveItemEvent e) {
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
		// No item movement, added to prevent debug spamming
		if (sourceBlock.equals(targetBlock)) return;


		Inventory sourceInventory = e.getSource();
		Inventory targetInventory = ((BlockInventoryHolder) targetBlock.getState()).getInventory();

		int freeSpace = getInvFreeAmount(targetInventory, e.getItem());

		for (ItemStack item : sourceInventory.getContents()) {
			if (freeSpace == 0) return;
			if (item != null && item.isSimilar(e.getItem())) {
				int removeAmount = Math.min(item.getAmount(), freeSpace);

				freeSpace -= removeAmount;

				// ItemStack to be moved to new hopper
				ItemStack movingItem = new ItemStack(item);
				movingItem.setAmount(removeAmount);

				// Removes item from source inventory
				item.setAmount(item.getAmount() - removeAmount);

				// Adds item to destination inventory
				targetInventory.addItem(movingItem);
				if (Debug)
					Bukkit.broadcastMessage("Added " + removeAmount + " " + movingItem.getType() + " to target hopper");
			}
		}

		if (MoreDebug)
			Bukkit.broadcastMessage("Source Hopper: " + sourceBlock.getX() + "," + sourceBlock.getY() + "," + sourceBlock.getZ());
		if (MoreDebug)
			Bukkit.broadcastMessage("Target Hopper: " + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ());
	}

	@EventHandler
	public void onInvItemMove(InventoryMoveItemEvent e) {
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
