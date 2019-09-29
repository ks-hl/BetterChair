package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener{

	private static Plugin instance;
	public static Plugin getInstance() {
		return instance;
	}
	
	private Map<World, WorldChairs> worlds = new HashMap<World, WorldChairs>();
	
	public Plugin() {}
	
	@Override
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(this, this);
		for(World world : Bukkit.getWorlds()) {
			WorldChairs worldChairs = new WorldChairs(world);
			Bukkit.getPluginManager().registerEvents(worldChairs, this);
			worlds.put(world, worldChairs);
		}
	}
	@Override
	public void onDisable() {
		for(WorldChairs chairs : worlds.values()) chairs.disable();
	}
	
	@EventHandler
	public void onWorldLoad(WorldInitEvent event) {
		WorldChairs worldChairs = new WorldChairs(event.getWorld());
		Bukkit.getPluginManager().registerEvents(worldChairs, this);
		worlds.put(event.getWorld(), worldChairs);
	}
	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) {
		if(worlds.containsKey(event.getWorld()) == false) return;
		WorldChairs chairs = worlds.get(event.getWorld());
		worlds.remove(event.getWorld());
		chairs.disable();
	}

	
}
