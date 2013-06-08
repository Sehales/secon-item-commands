package net.sehales.scitemcmds;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PlayerListener implements Listener {

	@EventHandler()
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof Player) {
			Player p = (Player) e.getInventory().getHolder();
			if (!p.isOnline())
				p.saveData();
		}
	}
}
