package net.sehales.scitemcmds;

import net.sehales.secon.addon.SeConAddon;
import net.sehales.secon.annotations.SeConAddonHandler;

import org.bukkit.plugin.Plugin;

@SeConAddonHandler()
public class ItemCmdCollection extends SeConAddon {

	private void initLanguage() {
		addLanguageInfoNode("clearinventory.inv-cleared-msg", "<green><sender> <gold>has cleared your inventory");
		addLanguageInfoNode("clearinventory.sender-cleared-msg", "<gold>You have cleared the inventory of <green><player>");
		addLanguageInfoNode("clearinventory.inv-cleared-id-msg", "<green><sender> <gold>has deleted all items with id <grey>'<id>' <gold>from your inventory");
		addLanguageInfoNode("clearinventory.sender-inv-cleared-id-msg", "<gold>You have deleted all items with id <grey>'<id>' <gold>from the inventory of <green><player>");
		addLanguageInfoNode("clearinventory.cant-clear-inventory-msg", "<red>Something went wrong, you can't clear that players inventory");
		addLanguageInfoNode("createitem.item-created-msg", "<gold>You have added the item <grey>'<red><name><grey>' <gold>to the config");
		addLanguageInfoNode("createitem.item-not-created-msg", "<red>You can't create this item! Maybe there is already an item with that name?");
		addLanguageInfoNode("enchant.available-enchantments", "<gold>Only the following enchantments are available: <enchantments>");
		addLanguageInfoNode("repair.all-repaired", "<gold>All items has been repaired");
		addLanguageInfoNode("repair.not-repairable", "<red>You can't repair that item!");
	}

	@Override
	protected void onDisable() {

	}

	@Override
	protected boolean onEnable(Plugin secon) {
		initLanguage();
		ICUtils utils = new ICUtils(this);
		registerCommands(new ItemCommands(this, utils));
		return true;
	}

}
