package de.Kurfat.Java.Minecraft.BetterChair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.Kurfat.Java.Minecraft.BetterChair.Events.PlayerChairCreateEvent;
import de.Kurfat.Java.Minecraft.BetterChair.Events.PlayerChairSwitchEvent;
import de.Kurfat.Java.Minecraft.BetterChair.Types.BedChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.BlockChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.CarpetChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.Chair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SlapChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SnowChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.StairChair;

public class BetterChair extends JavaPlugin implements Listener{

	/*
	 * Spigot Version search changed.
	 * BlockChair geändert
	 * Der PlayerisOnGround() Check muss ggf. noch geändert werden.
	 * Disable brauch deaktivirung der stühle
	 * 
	 */
	
	public static BetterChair INSTANCE;
	public static void info(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "BetterChair" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " " + message + ChatColor.RESET);
	}
	public static void warn(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "BetterChair" + ChatColor.DARK_GRAY + "]" + ChatColor.YELLOW + " " + message + ChatColor.RESET);
	}
	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "BetterChair" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + " " + message + ChatColor.RESET);
	}

	private static boolean IS_STARTED = false;
	private static File SETTINGS_FILE;
	protected static Settings SETTINGS;
	private static File USERS_FILE;
	protected static HashMap<UUID, Boolean> USERS;
	
	public static WorldGuardAddon WORLDGUARDADDON;
	
	public BetterChair() {}
	
	private IChair createChair(Player player, Block block) {
		if(SETTINGS.getGlobal().get(ChairType.STAIR)) try { return new StairChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.getGlobal().get(ChairType.SLAP)) try { return new SlapChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.getGlobal().get(ChairType.BED)) try { return new BedChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.getGlobal().get(ChairType.SNOW)) try { return new SnowChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.getGlobal().get(ChairType.CARPET)) try { return new CarpetChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.getGlobal().get(ChairType.BLOCK)) try { return new BlockChair(player, block); } catch (TypeParseException e) {}
		return null;
	}
	
	@Override
	public void onLoad() {
		if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null) WORLDGUARDADDON = new WorldGuardAddon();
	}
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		// CHECK SPIGOT VERSION
		int version = SpigotVersion.current();
		if(version < 14) {
			error("This plugin is not supported under 1.14.");
			return;
		}
		else if(version < 17) {
			error("This version of the plugin does not support your spigot version. Please use BetterChair version 1.7.1 for 1.14 - 1.16: https://www.spigotmc.org/resources/betterchair.71734/history");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				try {
					InputStream in = new URL("https://api.spigotmc.org/legacy/update.php?resource=71734").openConnection().getInputStream();
					List<Byte> list = new ArrayList<Byte>();
					try {
						while (true) {
							byte b = (byte) in.read();
							if(b == -1) break;
							list.add(b);
						}
					} catch (Exception e) {}
					byte[] bytes = new byte[list.size()];
					for(int i = 0; i < list.size(); i++) bytes[i] = list.get(i);
					String version = new String(bytes);
					if(getDescription().getVersion().equals(version)) info("Plugin is up to date.");
					else warn("Version " + version + " is available: https://www.spigotmc.org/resources/betterchair.71734/");
				} catch (IOException e) {
					error("No connection to the Spigot server to check for updates.");
				}
			}
		});		
		
		// LOAD FILES
		String path = getDataFolder().getAbsolutePath();
		Gson gson = new GsonBuilder().create();
		try {
			SETTINGS_FILE  = new File(path + "/settings.json");
			SETTINGS = gson.fromJson(new FileReader(SETTINGS_FILE), Settings.class);
			if(SETTINGS.global == null || SETTINGS.global.size() == 0 || SETTINGS.message == null) {
				error("Settings could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
				return;
			}
			info("Settings was loaded.");
		} catch (FileNotFoundException e) {
			SETTINGS = new Settings();
			SETTINGS.global = new HashMap<ChairType, Boolean>();
			for(ChairType type : ChairType.values()) SETTINGS.global.put(type, true);
			warn("Settings not found. A new one is created.");
		} catch (Exception e) {
			error("Settings could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
			return;
		}
		try {
			USERS_FILE  = new File(path + "/users.json");
			USERS = gson.fromJson(new FileReader(USERS_FILE), new TypeToken<HashMap<UUID, Boolean>>(){}.getType());
			info("Users was loaded.");
		} catch (FileNotFoundException e) {
			USERS = new HashMap<UUID, Boolean>();
			warn("Users not found. A new one is created.");
		} catch (Exception e) {
			error("Users could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
			return;
		}
		
		IS_STARTED = true;
		
		// SAVE FILES
		try { save(); } catch (IOException ex) { ex.printStackTrace(); }
		
		// START
		EntityPassengerRotate.INSTANCE.start();
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginCommand("chair").setExecutor(new Command_Chair());
	}
	public void save() throws IOException {
		if(getDataFolder().exists() == false) getDataFolder().mkdirs();
		
		if(SETTINGS_FILE.exists() == false) SETTINGS_FILE.createNewFile();
		FileWriter writer = new FileWriter(SETTINGS_FILE);
		writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(SETTINGS));
		writer.flush();
		writer.close();
		
		if(USERS_FILE.exists() == false) USERS_FILE.createNewFile();
		writer = new FileWriter(USERS_FILE);
		writer.write(new GsonBuilder().create().toJson(USERS));
		writer.flush();
		writer.close();
	}
	@Override
	public void onDisable() {
		if(IS_STARTED == false) return;
		new ArrayList<>(Chair.CACHE_BY_PLAYER.values()).forEach(c -> {
			c.eject();
			c.remove();
		});;
		HandlerList.unregisterAll((Listener)this);
		EntityPassengerRotate.INSTANCE.stop();
		try { save(); } catch (IOException ex) { ex.printStackTrace(); }
	}
	
	@Deprecated
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// CHECK PLAYER
		if(event.getHand() != EquipmentSlot.HAND 
				|| event.hasItem() 
				|| event.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| event.getPlayer().isOnGround() == false
				|| USERS.containsKey(event.getPlayer().getUniqueId()) && USERS.get(event.getPlayer().getUniqueId()) == false) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		// CHECK BLOCK
		if(block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2 
				|| Chair.CACHE_BY_BLOCK.containsKey(block)
				|| Chair.CACHE_BY_PLAYER.containsKey(player)
				|| block.getRelative(BlockFace.UP).isPassable() == false) return;
		// USE BLOCK
		IChair chair = createChair(player, block);
		if(chair == null) return;
		PlayerChairCreateEvent customEvent = new PlayerChairCreateEvent(player, chair);
		Bukkit.getPluginManager().callEvent(customEvent);
		if(customEvent.isCancelled()) return;
		if(WORLDGUARDADDON != null && WORLDGUARDADDON.check(player, chair) == false) return;
		chair.spawn();
		Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, chair, true));
	}
	
	public static enum ChairType {
		STAIR, SLAP, BED, SNOW, CARPET, BLOCK;
	}
}
