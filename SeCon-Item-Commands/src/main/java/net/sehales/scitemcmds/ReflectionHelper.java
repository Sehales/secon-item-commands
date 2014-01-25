package net.sehales.scitemcmds;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ReflectionHelper {

	private static String  nmspkg;
	private static String  cbpkg;
	private static boolean enabled;

	public static Class<?> getCraftInventoryClass() {
		try {
			return Class.forName(cbpkg + ".inventory.CraftInventory");
		} catch (ClassNotFoundException e) {
			return null;

		}
	}

	public static Player getOfflinePlayer(String name, World world) {
		if (!enabled)
			return null;
		try {
			//get the MinecraftServer object
			Object minecraftServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());

			//get the World object
			Object nmsWorld = world.getClass().getMethod("getHandle").invoke(world);

			//get the classes
			Class<?> entityPlayerClass = Class.forName(nmspkg + ".EntityPlayer");
			Class<?> worldClass = Class.forName(nmspkg + ".World");
			Class<?> pimClass = Class.forName(nmspkg + ".PlayerInteractManager");

			Constructor<?> pimCon = pimClass.getConstructor(worldClass);

			//get the needed Constructor of EntityPlayer
			Constructor<?> entityPlayerCon = entityPlayerClass.getConstructor(Class.forName(nmspkg + ".MinecraftServer"), worldClass, String.class, pimClass);

			//create a new instance of EntityPlayer
			Object entityPlayer = entityPlayerCon.newInstance(minecraftServer, nmsWorld, name, pimCon.newInstance(nmsWorld));

			//get the BukkitEntity of it
			Object bukkitEntity = entityPlayerClass.getMethod("getBukkitEntity").invoke(entityPlayer);

			//cast it to Player, load the data and return the player
			if (bukkitEntity instanceof Player) {
				Player player = (Player) bukkitEntity;
				if (player != null) {
					player.loadData();
					return player;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setCBPackage(String cb) {
		cbpkg = cb;
	}

	public static void setEnabled(boolean enabled) {
		ReflectionHelper.enabled = enabled;
	}

	public static void setNMSPackage(String nms) {
		nmspkg = nms;
	}
}
