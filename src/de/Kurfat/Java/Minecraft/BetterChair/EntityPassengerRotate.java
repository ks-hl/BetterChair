package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityPassengerRotate implements Runnable, Listener{
	
	public static final EntityPassengerRotate INSTANCE = new EntityPassengerRotate();
	
	private Map<Entity, Location> STORAGE = new HashMap<Entity, Location>();
	private BukkitTask task;
	
	public EntityPassengerRotate() {}

	public void start() {
		task = Bukkit.getScheduler().runTaskTimer(BetterChair.INSTANCE, this, 0, 1);
		Bukkit.getPluginManager().registerEvents(this, BetterChair.INSTANCE);
	}
	public void stop() {
		HandlerList.unregisterAll(this);
		task.cancel();
	}
	@Override
	public void run() {
		for(Entry<Entity, Location> entry : new ArrayList<>(STORAGE.entrySet())) if(STORAGE.containsKey(entry.getKey())){
			Entity entity = entry.getKey();
			Location oldLocation = entry.getValue();
			Location newLocation = entity.getLocation();
			STORAGE.put(entity, newLocation);
			if(oldLocation.getYaw() == newLocation.getYaw() && oldLocation.getPitch() == newLocation.getPitch()) continue;
			EntityPassengerRotateEvent event = new EntityPassengerRotateEvent(entity, oldLocation, newLocation);
			Bukkit.getPluginManager().callEvent(event);
		}
	}
	
	@EventHandler
	public void onEntityMount(EntityMountEvent event) {
		STORAGE.put(event.getEntity(), event.getEntity().getLocation());
	}
	@EventHandler
	public void onEntityMount(EntityDismountEvent event) {
		STORAGE.remove(event.getEntity());
	}
	
	public static class EntityPassengerRotateEvent extends EntityEvent{
		
		private static final HandlerList HANDLERS = new HandlerList();
		
		private Location oldLocation;
		private Location newLocation;
		
		public EntityPassengerRotateEvent(Entity entity, Location oldLocation, Location newLocation) {
			super(entity);
			this.oldLocation = oldLocation;
			this.newLocation = newLocation;
		}
		
		public Location getOldLocation() {
			return oldLocation;
		}
		public Location getNewLocation() {
			return newLocation;
		}
		@Override
		public HandlerList getHandlers() {
			return HANDLERS;
		}
		public static HandlerList getHandlerList() {
			return HANDLERS;
		}
	}
}
