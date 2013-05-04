package net.sehales.scitemcmds;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ICUtils {

	private static List<Integer> repairableItems = new LinkedList<Integer>();

	static {
		for (int i = 256; i <= 259; i++)
			repairableItems.add(i);
		for (int i = 267; i <= 279; i++)
			repairableItems.add(i);
		for (int i = 283; i <= 286; i++)
			repairableItems.add(i);
		for (int i = 290; i <= 294; i++)
			repairableItems.add(i);
		for (int i = 298; i <= 317; i++)
			repairableItems.add(i);
		repairableItems.add(359);
		repairableItems.add(Material.BOW.getId());
		repairableItems.add(Material.FISHING_ROD.getId());
		repairableItems.add(Material.CARROT_STICK.getId());
	}

	private ItemCmdCollection    ic;

	ICUtils(ItemCmdCollection ic) {
		this.ic = ic;
	}

	public int clearInventory(Inventory inv, String option) {
		try {
			if (option.equals("all")) {
				inv.clear();
				return 0;
			} else {
				int i = Integer.parseInt(option);
				inv.remove(i);
				return 1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public boolean isRepairable(int itemId) {
		return repairableItems.contains(itemId);
	}

	public boolean isRepairable(ItemStack item) {
		return repairableItems.contains(item.getTypeId());
	}

	public boolean isRepairable(Material mat) {
		return repairableItems.contains(mat.getId());
	}
}
