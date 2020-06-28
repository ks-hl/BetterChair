package de.Kurfat.Java.Minecraft.BetterChair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
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

	public static BetterChair INSTANCE;
	private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File FILE;
	private static HashMap<ChairType, Boolean> SETTINGS = new HashMap<ChairType, Boolean>();
	private static WorldGuardAddon WORLDGUARDADDON;
	
	public BetterChair() {}
	
	private IChair createChair(Player player, Block block) {
		if(SETTINGS.get(ChairType.STAIR)) try { return new StairChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.SLAP)) try { return new SlapChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.BED)) try { return new BedChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.SNOW)) try { return new SnowChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.CARPET)) try { return new CarpetChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.BLOCK)) try { return new BlockChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.STAIR)) try { return new StairChair(player, block); } catch (TypeParseException e) {}
		if(SETTINGS.get(ChairType.STAIR)) try { return new StairChair(player, block); } catch (TypeParseException e) {}
		return null;
	}
	
	@Override
	public void onLoad() {
		if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null) WORLDGUARDADDON = new WorldGuardAddon();
	}
	
	@Override
	public void onEnable() {
		INSTANCE = this;
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
					if(getDescription().getVersion().equals(version)) System.out.println("[BetterChair] Plugin is up to date.");
					else System.out.println("[BetterChair] Version \"" + version + "\" is available: https://www.spigotmc.org/resources/betterchair.71734/");
				} catch (IOException e) {
					System.out.println("[BetterChair] No connection to the Spigot server to check for updates.");
				}
			}
		});
		
		FILE = new File(getDataFolder().getAbsolutePath() +  "/settings.json");
		try {
			SETTINGS = GSON.fromJson(new FileReader(FILE), new TypeToken<HashMap<ChairType, Boolean>>(){}.getType());
		} catch (Exception e) {
			SETTINGS = new HashMap<ChairType, Boolean>();
			for(ChairType type : ChairType.values()) SETTINGS.put(type, true);
			try { save(); } catch (IOException e1) {e1.printStackTrace(); }
		}
		try {
			SpigotVersion version = SpigotVersion.currentVersion();
			if(version != SpigotVersion.VERSION_UNKNOWN) BlockChair.start(version);
			else {
				System.out.println("[BetterChair] This version is not supported!");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		EntityPassengerRotate.INSTANCE.start();
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	public void save() throws IOException {
		if(FILE.exists() == false) {
			FILE.getParentFile().mkdirs();
			FILE.createNewFile();
		}
		FileWriter writer = new FileWriter(FILE);
		writer.write(GSON.toJson(SETTINGS));
		writer.flush();
		writer.close();
	}
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Listener)this);
		EntityPassengerRotate.INSTANCE.stop();
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND 
				|| event.hasItem() 
				|| event.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| event.getPlayer().isOnGround() == false) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2 
				|| Chair.CACHE_BY_BLOCK.containsKey(block)
				|| Chair.CACHE_BY_PLAYER.containsKey(player)
				|| block.getRelative(BlockFace.UP).isPassable() == false) return;
		
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
