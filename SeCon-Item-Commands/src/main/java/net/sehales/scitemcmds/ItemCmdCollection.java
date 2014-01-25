
package net.sehales.scitemcmds;

import net.sehales.secon.addon.Addon;

public class ItemCmdCollection extends Addon {
    
    void addConfigNode(String path, Object value) {
        if (!configContains(path)) {
            getConfig().set(path, value);
        }
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
        addLanguageNode("clearinventory.inv-cleared-msg", "<green><sender> <gold>has cleared your inventory");
        addLanguageNode("clearinventory.sender-cleared-msg", "<gold>You have cleared the inventory of <green><player>");
        addLanguageNode("clearinventory.inv-cleared-id-msg", "<green><sender> <gold>has deleted all items with id <grey>'<id>' <gold>from your inventory");
        addLanguageNode("clearinventory.sender-inv-cleared-id-msg", "<gold>You have deleted all items with id <grey>'<id>' <gold>from the inventory of <green><player>");
        addLanguageNode("clearinventory.cant-clear-inventory-msg", "<red>Something went wrong, you can't clear that players inventory");
        addLanguageNode("createitem.item-created-msg", "<gold>You have added the item <grey>'<red><name><grey>' <gold>to the config");
        addLanguageNode("createitem.item-not-created-msg", "<red>You can't create this item! Maybe there is already an item with that name?");
        addLanguageNode("enchant.available-enchantments", "<gold>Only the following enchantments are available: <enchantments>");
        addLanguageNode("repair.all-repaired", "<gold>All items has been repaired");
        addLanguageNode("repair.not-repairable", "<red>You can't repair that item!");
        addLanguageNode("renameitem.no-valid-item", "<red>Air is not a valid item");
    }
    
    @Override
    protected boolean onEnable() {
        initLanguage();
        initConfig();
        registerListener(new PlayerListener());
        
        // String nmspkg = getConfig().getString("nms-package",
        // "net.minecraft.server");
        // try {
        // Class.forName(nmspkg + ".MinecraftServer");
        // } catch (ClassNotFoundException e) {
        // getLogger().severe("InventoryCmds",
        // "Wrong nms package, please check your config! (it have to look like 'net.minecraft.server.VERSION'");
        // getLogger().info("InventoryCmds",
        // "Usage of offline player support has been turned off!");
        // ReflectionHelper.setEnabled(false);
        // }
        //
        // String cbpkg = getConfig().getString("cb-package",
        // "org.bukkit.craftbukkit");
        // try {
        // Class.forName(cbpkg + ".CraftServer");
        // } catch (ClassNotFoundException e) {
        // getLogger().severe("InventoryCmds",
        // "Wrong cb package, please check your config! (it have to look like 'org.bukkit.craftbukkit.VERSION'");
        // getLogger().info("InventoryCmds",
        // "Usage of offline player support has been turned off!");
        // ReflectionHelper.setEnabled(false);
        // }
        
        // ReflectionHelper.setNMSPackage(nmspkg);
        // ReflectionHelper.setCBPackage(cbpkg);
        ICUtils utils = new ICUtils(this);
        registerCommandsFromObject(new ItemCommands(this, utils));
        return true;
    }
}
