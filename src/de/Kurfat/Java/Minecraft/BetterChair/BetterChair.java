package de.Kurfat.Java.Minecraft.BetterChair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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

import de.Kurfat.Java.Minecraft.BetterChair.Types.BedChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.BlockChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.CarpetChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.Chair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SlapChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SnowChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.StairChair;

public class BetterChair extends JavaPlugin implements Listener{

	public static BetterChair INSTANCE;
	private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File FILE;
	private static HashMap<SitType, Boolean> SETTINGS = new HashMap<SitType, Boolean>();;
	
	public BetterChair() {}
	
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
					if(getDescription().getVersion().equals(version)) {
						System.out.println("[BetterChair] Plugin is up to date.");
						return;
					}
					System.out.println("[BetterChair] Version \"" + version + "\" is available: https://www.spigotmc.org/resources/betterchair.71734/");
					return;
				} catch (IOException e) {
					System.out.println("[BetterChair] No connection to the Spigot server to check for updates.");
				}
			}
		});
		
		
		FILE = new File(getDataFolder().getAbsolutePath() +  "/settings.json");
		try {
			SETTINGS = GSON.fromJson(new FileReader(FILE), new TypeToken<HashMap<SitType, Boolean>>(){}.getType());
		} catch (Exception e) {
			SETTINGS = new HashMap<SitType, Boolean>();
			for(SitType type : SitType.values()) SETTINGS.put(type, true);
			try {
				save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
		
		if(SETTINGS.get(SitType.STAIR)) {
			try {
				Chair chair = new StairChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
		
		if(SETTINGS.get(SitType.SLAP)) {
			try {
				Chair chair = new SlapChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
		
		if(SETTINGS.get(SitType.BED)) {
			try {
				Chair chair = new BedChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
		
		if(SETTINGS.get(SitType.SNOW)) {
			try {
				Chair chair = new SnowChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
		
		if(SETTINGS.get(SitType.CARPET)) {
			try {
				Chair chair = new CarpetChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
		
		if(SETTINGS.get(SitType.BLOCK)) {
			try {
				Chair chair = new BlockChair(player, block);
				chair.spawn();
				return;
			} catch (TypeParseException e) {}
		}
	}
	
	public static enum SitType {
		STAIR, SLAP, BED, SNOW, CARPET, BLOCK;
	}
}
