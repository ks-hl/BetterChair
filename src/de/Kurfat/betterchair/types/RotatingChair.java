package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import de.kurfat.betterchair.EntityPassengerRotate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public abstract class RotatingChair extends Chair {
    public RotatingChair(BetterChair.ChairType type, Player player, Block block, Location location) {
        super(type, player, block, location);
    }

    public RotatingChair(BetterChair.ChairType type, Player player, Block block) {
        super(type, player, block);
    }

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotate.EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }
}
