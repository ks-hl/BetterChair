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
import java.util.Map.Entry;

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
import com.google.gson.internal.LinkedTreeMap;
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

	public static BetterChair INSTANCE;
	private static void message(ChatColor color, String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "BetterChair" + ChatColor.DARK_GRAY + "]" + color + " " + message + ChatColor.RESET);
	}
	private static void info(String message) {
		message(ChatColor.GREEN, message);
	}
	private static void warn(String message) {
		message(ChatColor.YELLOW, message);
	}
	private static void error(String message) {
		message(ChatColor.RED, message);
	}

	private static boolean IS_STARTED = false;
	private static File SETTINGS_FILE;
	protected static Settings SETTINGS;
	private static File USERS_FILE;
	protected static HashMap<UUID, Boolean> USERS;
	private static LinkedTreeMap<ChairType, Class<? extends IChair>> BUILDERS = new LinkedTreeMap<>();

	public static WorldGuardAddon WORLDGUARDADDON;
	
	public BetterChair() {
		BUILDERS.put(ChairType.STAIR, StairChair.class);
		BUILDERS.put(ChairType.SLAP, SlapChair.class);
		BUILDERS.put(ChairType.BED, BedChair.class);
		BUILDERS.put(ChairType.SNOW, SnowChair.class);
		BUILDERS.put(ChairType.CARPET, CarpetChair.class);
		BUILDERS.put(ChairType.BLOCK, BlockChair.class);
	}
	
	public static IChair createChair(Player player, Block block) throws ChairException {
		for(Entry<ChairType, Class<? extends IChair>> builder : BUILDERS.entrySet()) if(SETTINGS.getGlobal().get(builder.getKey())) {
			IChair chair;
			try {
				chair = builder.getValue().getConstructor(Player.class, Block.class).newInstance(player, block);
				if(chair == null) continue;
			} catch (Exception e) {
				continue;
			}
			PlayerChairCreateEvent customEvent = new PlayerChairCreateEvent(player, chair);
			Bukkit.getPluginManager().callEvent(customEvent);
			if(customEvent.isCancelled()) throw new ChairException(player, block, "The block creation was blocked by an unknown plugin.");
			if(WORLDGUARDADDON != null && WORLDGUARDADDON.check(player, chair) == false) throw new ChairException(player, block, "Creating the block was blocked by WorldGuard.");
			chair.spawn();
			Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, chair, true));
			// System.out.println(player.getName() + " " + chair.getClass().getSimpleName() + " " + block.getBlockData().getClass().getSimpleName() + " " + block.getType());
			// for(Class<?> i : block.getBlockData().getClass().getInterfaces()) System.out.println("- " + i.getSimpleName());
			// for(Class<?> i : block.getBlockData().getClass().getClasses()) System.out.println("- " + i.getSimpleName());
			return chair;
		}
		throw new ChairException(player, block, "This block can not be used as a chair.");
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
			for(ChairType type : ChairType.values()) if(SETTINGS.global.containsKey(type) == false) {
				SETTINGS.global.put(type, true);
				warn("The chair \""+ type + "\" was not set in the settings and now set to \"true\"!");
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
		if(event.getHand() != EquipmentSlot.HAND 
				|| event.hasItem() 
				|| event.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| event.getPlayer().isOnGround() == false
				|| USERS.containsKey(event.getPlayer().getUniqueId()) && USERS.get(event.getPlayer().getUniqueId()) == false) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2 
				|| Chair.CACHE_BY_BLOCK.containsKey(block)
				|| Chair.CACHE_BY_PLAYER.containsKey(player)
				|| block.getRelative(BlockFace.UP).isPassable() == false) return;
		try { createChair(player, block); } catch (ChairException e) {}
	}
	
	public static enum ChairType {
		STAIR, SLAP, BED, SNOW, CARPET, BLOCK;
	}
}
