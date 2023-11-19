package de.kurfat.betterchair.types;

import de.kurfat.betterchair.events.EntityPassengerRotateEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public abstract class RotatingChair extends Chair {
    public RotatingChair(ChairType type, Player player, Block block, Location location) {
        super(type, player, block, location);
    }

    public RotatingChair(ChairType type, Player player, Block block) {
        super(type, player, block);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }
}
