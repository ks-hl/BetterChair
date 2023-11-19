package de.kurfat.betterchair.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import javax.annotation.Nonnull;

public class EntityPassengerRotateEvent extends EntityEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Location oldLocation;
    private final Location newLocation;

    public EntityPassengerRotateEvent(Entity entity, Location oldLocation, Location newLocation) {
        super(entity);
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @SuppressWarnings("unused")
    public Location getOldLocation() {
        return oldLocation;
    }

    @SuppressWarnings("unused")
    public Location getNewLocation() {
        return newLocation;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
