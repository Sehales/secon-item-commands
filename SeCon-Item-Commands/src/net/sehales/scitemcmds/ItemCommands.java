
package net.sehales.scitemcmds;

import net.sehales.secon.SeCon;
import net.sehales.secon.command.CommandType;
import net.sehales.secon.command.MethodCommandHandler;
import net.sehales.secon.command.SeConCommand;
import net.sehales.secon.config.ItemConfig;
import net.sehales.secon.config.LanguageConfig;
import net.sehales.secon.utils.MiscUtils;
import net.sehales.secon.utils.mc.ChatUtils;
import net.sehales.secon.utils.mc.ItemUtils;
import net.sehales.secon.utils.string.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommands {
    
    private static String     enchantments;
    private ItemCmdCollection ic;
    private ICUtils           utils;
    private LanguageConfig    lang       = SeCon.getInstance().getLang();
    private ItemConfig        itemConfig = SeCon.getInstance().getItemConfig();
    
    static {
        StringBuilder sb = new StringBuilder();
        for (Enchantment e : Enchantment.values()) {
            sb.append("<grey>" + e.getName().toLowerCase() + "<gold>, ");
        }
        enchantments = sb.substring(0, sb.length() - 2);
    }
    
    ItemCommands(ItemCmdCollection ic, ICUtils utils) {
        this.ic = ic;
        this.utils = utils;
    }
    
    @MethodCommandHandler(name = "clearinventory", description = "<darkaqua>clear the whole inventory of yourself or another player or remove only a specified item;<darkaqua>usage: /clearinventory", permission = "secon.command.clearinventory", additionalPerms = "other:secon.command.clearinventory.other", aliases = "clearinv,ci")
    public void onClearInventoryCmd(CommandSender sender, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (args.length > 1 && MiscUtils.hasPermission(sender, cmd.getPermission("other"), true)) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    ChatUtils.sendFormattedMessage(sender, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                    return;
                }
                int result = utils.clearInventory(p.getInventory(), args[1]);
                switch (result) {
                    case -1: {
                        ChatUtils.sendFormattedMessage(sender, ic.getLanguageNode("clearinventory.cant-clear-inventory-msg"));
                        return;
                    }
                    case 0: {
                        ChatUtils.sendFormattedMessage(sender, ic.getLanguageNode("clearinventory.sender-inv-cleared-msg").replace("<player>", p.getName()));
                        ChatUtils.sendFormattedMessage(p, ic.getLanguageNode("clearinventory.inv-cleared-msg").replace("<sender>", sender.getName()));
                        return;
                    }
                    case 1: {
                        ChatUtils.sendFormattedMessage(sender, ic.getLanguageNode("clearinventory.sender-inv-cleared-id-msg").replace("<id>", args[1]).replace("<player>", p.getName()));
                        ChatUtils.sendFormattedMessage(p, ic.getLanguageNode("clearinventory.inv-cleared-id-msg").replace("<id>", args[1]).replace("<sender>", sender.getName()));
                        return;
                    }
                }
            }
            if (sender instanceof Player) {
                if (utils.clearInventory(((Player) sender).getInventory(), args[0]) == -1) {
                    ChatUtils.sendFormattedMessage(sender, ic.getLanguageNode("clearinventory.cant-clear-inventory-msg"));
                }
                return;
            }
        } else if (sender instanceof Player) {
            if (utils.clearInventory(((Player) sender).getInventory(), "all") == -1) {
                ChatUtils.sendFormattedMessage(sender, ic.getLanguageNode("clearinventory.cant-clear-inventory-msg"));
            }
            return;
        }
    }
    
    @SuppressWarnings("deprecation")
    @MethodCommandHandler(name = "createitem", description = "<darkaqua>create an item out of the item you are holding in your hand for usage with /item and /give;<darkaqua>usage: /createitem [name]", aliases = "additem", type = CommandType.PLAYER)
    public void onCreateItemCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            ItemStack stack = player.getItemInHand();
            if (stack != null && stack.getTypeId() != 0) {
                if (itemConfig.addItem(args[0], stack)) {
                    ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("createitem.item-created-msg").replace("<name>", args[0]));
                } else {
                    ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("createitem.item-not-created-msg"));
                }
            }
        } else {
            ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    @MethodCommandHandler(name = "enchant", description = "<darkaqua>enchant the item you are currently holding in your hand;<darkaqua>usage: /enchant [silk_touch 1|all 10]", permission = "secon.command.enchant", type = CommandType.PLAYER)
    public void onEnchantCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            ItemStack item = player.getItemInHand();
            int level = 1;
            if (args.length > 1) {
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Enchantment e : Enchantment.values()) {
                    ItemUtils.addItemEnchantment(item, e, level, true);
                }
            } else {
                Enchantment e = null;
                try {
                    e = Enchantment.getByName(args[0].toUpperCase());
                } catch (Exception ex) {
                    ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("enchant.available-enchantments").replace("<enchantments>", enchantments));
                }
                if (e != null) {
                    ItemUtils.addItemEnchantment(item, e, level, true);
                } else {
                    ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("enchant.available-enchantments").replace("<enchantments>", enchantments));
                }
            }
        } else {
            ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    @MethodCommandHandler(name = "give", description = "<darkaqua>give an item to a specified player(must have the permission [permOfThatCmd].[itemid]|[custom item name];<darkaqua>usage: /give [player] [5:1|7|stone]", additionalPerms = "item:secon.command.give.<item>", permission = "secon.command.give")
    public void onGiveCmd(CommandSender sender, SeConCommand cmd, String[] args) {
        try {
            if (args.length > 0) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    ChatUtils.sendFormattedMessage(sender, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                    return;
                }
                if (args.length > 1) {
                    String[] values = args[1].split(":");
                    ItemStack stack = null;
                    if (MiscUtils.hasPermission(sender, cmd.getPermission("item").replace("<item>", values[0]), true)) {
                        stack = itemConfig.getItem(values[0]);
                        if (values.length > 1) {
                            short damage = Short.parseShort(values[1]);
                            stack.setDurability(damage);
                        }
                        if (args.length > 2) {
                            int amount = Integer.parseInt(args[2]);
                            stack.setAmount(amount);
                        }
                        if (stack != null) {
                            p.getInventory().addItem(stack);
                        } else {
                            ChatUtils.sendFormattedMessage(sender, lang.NOT_ENOUGH_ARGUMENTS);
                        }
                    }
                } else {
                    ChatUtils.sendFormattedMessage(sender, lang.NOT_ENOUGH_ARGUMENTS);
                }
            } else {
                ChatUtils.sendFormattedMessage(sender, lang.NOT_ENOUGH_ARGUMENTS);
            }
        } catch (Exception e) {
            ChatUtils.sendFormattedMessage(sender, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    @MethodCommandHandler(name = "item", description = "<darkaqua>get an item;<darkaqua>usage: /item [5:1|7|stone]", additionalPerms = "item:secon.command.item.<item>", permission = "secon.command.item", aliases = "i", type = CommandType.PLAYER)
    public void onItemCmd(Player player, SeConCommand cmd, String[] args) {
        try {
            if (args.length > 0) {
                String[] values = args[0].split(":");
                ItemStack stack = null;
                if (MiscUtils.hasPermission(player, cmd.getPermission("item").replace("<item>", values[0]), true)) {
                    stack = itemConfig.getItem(values[0]);
                    if (values.length > 1) {
                        short damage = Short.parseShort(values[1]);
                        stack.setDurability(damage);
                    }
                    if (args.length > 1) {
                        int amount = Integer.parseInt(args[1]);
                        stack.setAmount(amount);
                    }
                    if (stack != null) {
                        player.getInventory().addItem(stack);
                    } else {
                        ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
                    }
                }
            } else {
                ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
            }
        } catch (Exception e) {
            ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    @SuppressWarnings("deprecation")
    @MethodCommandHandler(name = "iteminfo", description = "<darkaqua>get information about the item in your hand;<darkaqua>usage: /iteminfo", permission = "secon.command.iteminfo", type = CommandType.PLAYER, aliases = "itemdb,getid,id")
    public void onItemInfoCmd(Player player, SeConCommand cmd, String[] args) {
        ItemStack item = player.getItemInHand();
        ChatUtils.sendFormattedMessage(player, "" + "id:" + item.getTypeId() + ":" + item.getDurability());
        ChatUtils.sendFormattedMessage(player, "" + item.toString());
    }
    
    @MethodCommandHandler(name = "more", description = "<darkaqua>get a full stack of the held item or a given number;<darkaqua>usage: /more [10]", permission = "secon.command.more", type = CommandType.PLAYER, aliases = "itemamount,stackitem,stack")
    public void onMoreCmd(Player sender, SeConCommand cmd, String[] args) {
        int amount = 64;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (Exception e) {
                ChatUtils.sendFormattedMessage(sender, lang.NOT_ENOUGH_ARGUMENTS);
            }
        }
        sender.getItemInHand().setAmount(amount);
    }
    
    @MethodCommandHandler(name = "openinventory", description = "<darkaqua>open the inventory of another player;<darkaqua>usage: /openinventory [player]", permission = "secon.command.openinventory", aliases = "oi,openinv,invsee", type = CommandType.PLAYER)
    public void onOpenInventoryCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                ChatUtils.sendFormattedMessage(player, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                return;
            }
            player.openInventory(p.getInventory());
        } else {
            ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    @MethodCommandHandler(name = "playerhead", description = "<darkaqua>get a player's head;<darkaqua>usage: /playerhead [player]", permission = "secon.command.playerhead", aliases = "head,phead", type = CommandType.PLAYER)
    public void onPlayerheadCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            player.getInventory().addItem(ItemUtils.getPlayerHead(args[0]));
        } else {
            player.getInventory().addItem(ItemUtils.getPlayerHead(player.getName()));
        }
    }
    
    @MethodCommandHandler(name = "renameitem", description = "<darkaqua>rename the item you are currently holding;<darkaqua>usage: /renameitem [name]", permission = "secon.command.renameitem", aliases = "ri,itemrename,renamei", type = CommandType.PLAYER)
    public void onRenameItem(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            ItemStack item = player.getItemInHand();
            
            if (item == null || item.getType().compareTo(Material.AIR) == 0) {
                ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("renameitem.no-valid-item"));
                return;
            }
            
            String name = ChatUtils.formatMessage(StringUtils.getStringOfArray(args, 0));
            player.setItemInHand(ItemUtils.setItemName(item, name));
        } else {
            ChatUtils.sendFormattedMessage(player, lang.NOT_ENOUGH_ARGUMENTS);
        }
    }
    
    // @MethodCommandHandler(name = "proxytest", type = CommandType.PLAYER)
    // public void onProxyTestCmd(Player player, SeConCommand cmd, String[]
    // args) {
    // player.openInventory(InventoryProxyManager.createProxy(player.getName(),
    // player.getInventory()).getInventory());
    // }
    
    @MethodCommandHandler(name = "repair", description = "<darkaqua>repair your currently held item or all items;<darkaqua>usage: /repair [all]", additionalPerms = "all:secon.command.repair.all", permission = "secon.command.repair", type = CommandType.PLAYER, aliases = "rep")
    public void onRepairCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("all") && MiscUtils.hasPermission(player, cmd.getPermission("all"), true)) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        if (utils.isRepairable(item)) {
                            item.setDurability((short) 0);
                        }
                    }
                }
                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (item != null) {
                        if (utils.isRepairable(item)) {
                            item.setDurability((short) 0);
                        }
                    }
                }
                ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("repair.all-repaired"));
            }
        } else {
            ItemStack item = player.getItemInHand();
            if (item != null) {
                if (utils.isRepairable(item)) {
                    item.setDurability((short) 0);
                } else {
                    ChatUtils.sendFormattedMessage(player, ic.getLanguageNode("repair.not-repairable"));
                }
            }
        }
    }
    
    @MethodCommandHandler(name = "setboots", description = "<darkaqua>replace the boots of another player(or yourself) with the item you are currently holding in your hand;<darkaqua>usage: /setboots [player]", permission = "secon.command.setboots", additionalPerms = "other:secon.command.setboots", type = CommandType.PLAYER)
    public void onSetBootsCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (MiscUtils.hasPermission(player, cmd.getPermission("other"), true)) {
                boolean offline = false;
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    p = ReflectionHelper.getOfflinePlayer(args[0], player.getWorld());
                    if (p == null) {
                        ChatUtils.sendFormattedMessage(player, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                        return;
                    } else {
                        offline = true;
                    }
                }
                player.setItemInHand(utils.switchArmor(p, player.getItemInHand(), ICUtils.ARMOR_TYPE_BOOTS, offline));
            }
        } else {
            player.setItemInHand(utils.switchArmor(player, player.getItemInHand(), ICUtils.ARMOR_TYPE_BOOTS, false));
        }
    }
    
    @MethodCommandHandler(name = "setchestplate", description = "<darkaqua>replace the chestplate of another player(or yourself) with the item you are currently holding in your hand;<darkaqua>usage: /setchestplate [player]", permission = "secon.command.setchestplate", additionalPerms = "other:secon.command.setboots", type = CommandType.PLAYER)
    public void onSetChestplateCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (MiscUtils.hasPermission(player, cmd.getPermission("other"), true)) {
                boolean offline = false;
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    p = ReflectionHelper.getOfflinePlayer(args[0], player.getWorld());
                    if (p == null) {
                        ChatUtils.sendFormattedMessage(player, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                        return;
                    } else {
                        offline = true;
                    }
                }
                player.setItemInHand(utils.switchArmor(p, player.getItemInHand(), ICUtils.ARMOR_TYPE_CHESTPLATE, offline));
            }
        } else {
            player.setItemInHand(utils.switchArmor(player, player.getItemInHand(), ICUtils.ARMOR_TYPE_CHESTPLATE, false));
        }
    }
    
    @MethodCommandHandler(name = "sethelmet", description = "<darkaqua>replace the helmet of another player(or yourself) with the item you are currently holding in your hand;<darkaqua>usage: /sethelmet [player]", permission = "secon.command.sethelmet", aliases = "sethead", additionalPerms = "other:secon.command.setboots", type = CommandType.PLAYER)
    public void onSetHelmetCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (MiscUtils.hasPermission(player, cmd.getPermission("other"), true)) {
                boolean offline = false;
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    p = ReflectionHelper.getOfflinePlayer(args[0], player.getWorld());
                    if (p == null) {
                        ChatUtils.sendFormattedMessage(player, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                        return;
                    } else {
                        offline = true;
                    }
                }
                player.setItemInHand(utils.switchArmor(p, player.getItemInHand(), ICUtils.ARMOR_TYPE_HELMET, offline));
            }
        } else {
            player.setItemInHand(utils.switchArmor(player, player.getItemInHand(), ICUtils.ARMOR_TYPE_HELMET, false));
        }
    }
    
    @MethodCommandHandler(name = "setleggings", description = "<darkaqua>replace the leggings of another player(or yourself) with the item you are currently holding in your hand;<darkaqua>usage: /setleggings [player]", permission = "secon.command.setleggings", additionalPerms = "other:secon.command.setboots", type = CommandType.PLAYER)
    public void onSetLeggingsCmd(Player player, SeConCommand cmd, String[] args) {
        if (args.length > 0) {
            if (MiscUtils.hasPermission(player, cmd.getPermission("other"), true)) {
                boolean offline = false;
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    p = ReflectionHelper.getOfflinePlayer(args[0], player.getWorld());
                    if (p == null) {
                        ChatUtils.sendFormattedMessage(player, lang.PLAYER_NOT_FOUND.replace("<player>", args[0]));
                        return;
                    } else {
                        offline = true;
                    }
                }
                player.setItemInHand(utils.switchArmor(p, player.getItemInHand(), ICUtils.ARMOR_TYPE_LEGGINGS, offline));
            }
        } else {
            player.setItemInHand(utils.switchArmor(player, player.getItemInHand(), ICUtils.ARMOR_TYPE_LEGGINGS, false));
        }
    }
}
