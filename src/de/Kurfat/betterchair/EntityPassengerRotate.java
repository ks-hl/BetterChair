package de.kurfat.betterchair;

import de.kurfat.betterchair.events.EntityPassengerRotateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EntityPassengerRotate implements Runnable, Listener {

    public static final EntityPassengerRotate INSTANCE = new EntityPassengerRotate();

    private final Map<Entity, Location> STORAGE = new HashMap<>();
    private BukkitTask task;

    public EntityPassengerRotate() {
    }

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
        for (Entry<Entity, Location> entry : new ArrayList<>(STORAGE.entrySet()))
            if (STORAGE.containsKey(entry.getKey())) {
                Entity entity = entry.getKey();
                Location oldLocation = entry.getValue();
                Location newLocation = entity.getLocation();
                STORAGE.put(entity, newLocation);
                if (oldLocation.getYaw() == newLocation.getYaw() && oldLocation.getPitch() == newLocation.getPitch())
                    continue;
                EntityPassengerRotateEvent event = new EntityPassengerRotateEvent(entity, oldLocation, newLocation);
                Bukkit.getPluginManager().callEvent(event);
            }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND) return;
        STORAGE.put(event.getEntity(), event.getEntity().getLocation());
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onEntityMount(EntityDismountEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND) return;
        STORAGE.remove(event.getEntity());
    }

}
