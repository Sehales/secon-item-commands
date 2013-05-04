package net.sehales.scitemcmds;

import net.sehales.secon.SeCon;
import net.sehales.secon.addon.SeConCommand;
import net.sehales.secon.annotations.SeConCommandHandler;
import net.sehales.secon.config.LanguageHelper;
import net.sehales.secon.enums.CommandType;
import net.sehales.secon.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommands {

	private static String     enchantments;
	private ChatUtils         chat = SeCon.getAPI().getChatUtils();
	private ItemCmdCollection ic;
	private ICUtils           utils;

	static {
		StringBuilder sb = new StringBuilder();
		for (Enchantment e : Enchantment.values())
			sb.append("<grey>" + e.getName().toLowerCase() + "<gold>, ");
		enchantments = sb.substring(0, sb.length() - 2);
	}

	ItemCommands(ItemCmdCollection ic, ICUtils utils) {
		this.ic = ic;
		this.utils = utils;
	}

	@SeConCommandHandler(name = "clearinventory", help = "<darkaqua>clear the whole inventory of yourself or another player or remove only a specified item;<darkaqua>usage: /clearinventory", permission = "secon.command.clearinventory", additionalPerms = "other:secon.command.clearinventory.other", aliases = "clearinv,ci")
	public void onClearInventoryCmd(CommandSender sender, SeConCommand cmd, String[] args) {
		if (args.length > 0) {
			if (args.length > 1 && SeCon.getAPI().getSeConUtils().hasPermission(sender, cmd.getPermission("other"), true)) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null) {
					chat.sendFormattedMessage(sender, LanguageHelper.INFO_PLAYER_NOT_EXIST.replace("<player>", args[0]));
					return;
				}
				int result = utils.clearInventory(p.getInventory(), args[1]);
				switch (result) {
					case -1: {
						chat.sendFormattedMessage(sender, ic.getLanguageInfoNode("clearinventory.cant-clear-inventory-msg"));
						return;
					}
					case 0: {
						chat.sendFormattedMessage(sender, ic.getLanguageInfoNode("clearinventory.sender-inv-cleared-msg").replace("<player>", p.getName()));
						chat.sendFormattedMessage(p, ic.getLanguageInfoNode("clearinventory.inv-cleared-msg").replace("<sender>", sender.getName()));
						return;
					}
					case 1: {
						chat.sendFormattedMessage(sender, ic.getLanguageInfoNode("clearinventory.sender-inv-cleared-id-msg").replace("<id>", args[1]).replace("<player>", p.getName()));
						chat.sendFormattedMessage(p, ic.getLanguageInfoNode("clearinventory.inv-cleared-id-msg").replace("<id>", args[1]).replace("<sender>", sender.getName()));
						return;
					}
				}
			}
			if (sender instanceof Player) {
				if (utils.clearInventory(((Player) sender).getInventory(), args[0]) == -1)
					chat.sendFormattedMessage(sender, ic.getLanguageInfoNode("clearinventory.cant-clear-inventory-msg"));
				return;
			}
		}
		chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
	}

	@SeConCommandHandler(name = "createitem", help = "<darkaqua>create an item out of the item you are holding in your hand for usage with /item and /give;<darkaqua>usage: /createitem [name]", aliases = "additem", type = CommandType.PLAYER)
	public void onCreateItemCmd(Player player, SeConCommand cmd, String[] args) {
		if (args.length > 0) {
			ItemStack stack = player.getItemInHand();
			if (stack != null && stack.getTypeId() != 0)
				if (SeCon.getAPI().getItemUtils().addItem(args[0], stack))
					chat.sendFormattedMessage(player, ic.getLanguageInfoNode("createitem.item-created-msg").replace("<name>", args[0]));
				else
					chat.sendFormattedMessage(player, ic.getLanguageInfoNode("createitem.item-not-created-msg"));
		} else
			chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
	}

	@SeConCommandHandler(name = "enchant", help = "<darkaqua>enchant the item you are currently holding in your hand;<darkaqua>usage: /enchant [silk_touch 1|all 10]", permission = "secon.command.enchant", type = CommandType.PLAYER)
	public void onEnchantCmd(Player player, SeConCommand cmd, String[] args) {
		if (args.length > 0) {
			ItemStack item = player.getItemInHand();
			int level = 1;
			if (args.length > 1)
				try {
					level = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
					return;
				}
			if (args[0].equalsIgnoreCase("all"))
				for (Enchantment e : Enchantment.values())
					SeCon.getAPI().getItemUtils().addItemEnchantment(item, e, level, true);
			else {
				Enchantment e = null;
				try {
					e = Enchantment.getByName(args[0].toUpperCase());
				} catch (Exception ex) {
					chat.sendFormattedMessage(player, ic.getLanguageInfoNode("enchant.available-enchantments").replace("<enchantments>", enchantments));
				}
				if (e != null)
					SeCon.getAPI().getItemUtils().addItemEnchantment(item, e, level, true);
				else
					chat.sendFormattedMessage(player, ic.getLanguageInfoNode("enchant.available-enchantments").replace("<enchantments>", enchantments));
			}
		} else
			chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
	}

	@SeConCommandHandler(name = "give", help = "<darkaqua>give an item to a specified player(must have the permission [permOfThatCmd].[itemid]|[custom item name];<darkaqua>usage: /give [player] [5:1|7|stone]", additionalPerms = "item:secon.command.give.<item>", permission = "secon.command.give")
	public void onGiveCmd(CommandSender sender, SeConCommand cmd, String[] args) {
		try {
			if (args.length > 0) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null) {
					chat.sendFormattedMessage(sender, LanguageHelper.INFO_PLAYER_NOT_EXIST.replace("<player>", args[0]));
					return;
				}
				if (args.length > 1) {
					String[] values = args[1].split(":");
					ItemStack stack = null;
					if (SeCon.getAPI().getSeConUtils().hasPermission(sender, cmd.getPermission("item").replace("<item>", values[0]), true)) {
						stack = SeCon.getAPI().getItemUtils().getItem(values[0]);
						if (values.length > 1) {
							short damage = Short.parseShort(values[1]);
							stack.setDurability(damage);
						}
						if (args.length > 2) {
							int amount = Integer.parseInt(args[2]);
							stack.setAmount(amount);
						}
						if (stack != null)
							p.getInventory().addItem(stack);
						else
							chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
					}
				} else
					chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
			} else
				chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
		} catch (Exception e) {
			chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
		}
	}

	@SeConCommandHandler(name = "item", help = "<darkaqua>get an item;<darkaqua>usage: /item [5:1|7|stone]", additionalPerms = "item:secon.command.item.<item>", permission = "secon.command.item", aliases = "i", type = CommandType.PLAYER)
	public void onItemCmd(Player player, SeConCommand cmd, String[] args) {
		try {
			if (args.length > 0) {
				String[] values = args[0].split(":");
				ItemStack stack = null;
				if (SeCon.getAPI().getSeConUtils().hasPermission(player, cmd.getPermission("item").replace("<item>", values[0]), true)) {
					stack = SeCon.getAPI().getItemUtils().getItem(values[0]);
					if (values.length > 1) {
						short damage = Short.parseShort(values[1]);
						stack.setDurability(damage);
					}
					if (args.length > 1) {
						int amount = Integer.parseInt(args[1]);
						stack.setAmount(amount);
					}
					if (stack != null)
						player.getInventory().addItem(stack);
					else
						chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
				}
			} else
				chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
		} catch (Exception e) {
			chat.sendFormattedMessage(player, LanguageHelper.INFO_WRONG_ARGUMENTS);
		}
	}

	@SeConCommandHandler(name = "iteminfo", help = "<darkaqua>get information about the item in your hand;<darkaqua>usage: /iteminfo", permission = "secon.command.iteminfo", type = CommandType.PLAYER, aliases = "itemdb,getid,id")
	public void onItemInfoCmd(Player player, SeConCommand cmd, String[] args) {
		ItemStack item = player.getItemInHand();
		chat.sendFormattedMessage(player, "" + "id:" + item.getTypeId() + ":" + item.getDurability());
		chat.sendFormattedMessage(player, "" + item.toString());
	}

	@SeConCommandHandler(name = "more", help = "<darkaqua>get a full stack of the held item or a given number;<darkaqua>usage: /more [10]", permission = "secon.command.more", type = CommandType.PLAYER, aliases = "itemamount,stackitem,stack")
	public void onMoreCmd(Player sender, SeConCommand cmd, String[] args) {
		int amount = 64;
		if (args.length > 0)
			try {
				amount = Integer.parseInt(args[0]);
			} catch (Exception e) {
				chat.sendFormattedMessage(sender, LanguageHelper.INFO_WRONG_ARGUMENTS);
			}
		sender.getItemInHand().setAmount(amount);
	}

	@SeConCommandHandler(name = "playerhead", help = "<darkaqua>get a player's head;<darkaqua>usage: /playerhead [player]", permission = "secon.command.playerhead", aliases = "head,phead", type = CommandType.PLAYER)
	public void onPlayerheadCmd(Player player, SeConCommand cmd, String[] args) {
		if (args.length > 0)
			player.getInventory().addItem(SeCon.getAPI().getItemUtils().getPlayerHead(args[0]));
		else
			player.getInventory().addItem(SeCon.getAPI().getItemUtils().getPlayerHead(player.getName()));
	}

	@SeConCommandHandler(name = "repair", help = "<darkaqua>repair your currently held item or all items;<darkaqua>usage: /repair [all]", additionalPerms = "all:secon.command.repair.all", permission = "secon.command.repair", type = CommandType.PLAYER, aliases = "rep")
	public void onRepairCmd(Player player, SeConCommand cmd, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("all") && SeCon.getAPI().getSeConUtils().hasPermission(player, cmd.getPermission("all"), true)) {
				for (ItemStack item : player.getInventory().getContents())
					if (item != null)
						if (utils.isRepairable(item))
							item.setDurability((short) 0);
				for (ItemStack item : player.getInventory().getArmorContents())
					if (item != null)
						if (utils.isRepairable(item))
							item.setDurability((short) 0);
				chat.sendFormattedMessage(player, ic.getLanguageInfoNode("repair.all-repaired"));
			}
		} else {
			ItemStack item = player.getItemInHand();
			if (item != null)
				if (utils.isRepairable(item))
					item.setDurability((short) 0);
				else
					chat.sendFormattedMessage(player, ic.getLanguageInfoNode("repair.not-repairable"));
		}
	}
}
