package net.sehales.scitemcmds;

import net.sehales.secon.addon.SeConAddon;
import net.sehales.secon.annotations.SeConAddonHandler;

import org.bukkit.plugin.Plugin;

@SeConAddonHandler()
public class ItemCmdCollection extends SeConAddon {

	void addConfigNode(String path, Object value) {
		if (!configContains(path))
			getConfig().set(path, value);
	}

	boolean configContains(String path) {
		return getConfig().contains(path);
	}

	private void initConfig() {
		addConfigNode("nms-package", "net.minecraft.server");
		addConfigNode("cb-package", "org.bukkit.craftbukkit");
		saveConfig();
	}

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
		initConfig();
		registerListener(new PlayerListener());

		String nmspkg = getConfig().getString("nms-package", "net.minecraft.server");
		try {
			Class.forName(nmspkg + ".MinecraftServer");
		} catch (ClassNotFoundException e) {
			getLogger().severe("InventoryCmds", "Wrong nms package, please check your config! (must look like 'net.minecraft.server.VERSION'");
			getLogger().info("InventoryCmds", "Usage of offline player support has been turned off!");
			ReflectionHelper.setEnabled(false);
		}

		String cbpkg = getConfig().getString("cb-package", "org.bukkit.craftbukkit");
		try {
			Class.forName(cbpkg + ".CraftServer");
		} catch (ClassNotFoundException e) {
			getLogger().severe("InventoryCmds", "Wrong cb package, please check your config! (must look like 'org.bukkit.craftbukkit.VERSION'");
			getLogger().info("InventoryCmds", "Usage of offline player support has been turned off!");
			ReflectionHelper.setEnabled(false);
		}

		ReflectionHelper.setNMSPackage(nmspkg);
		ReflectionHelper.setCBPackage(cbpkg);
		ICUtils utils = new ICUtils(this);
		registerCommands(new ItemCommands(this, utils));
		return true;
	}
}
