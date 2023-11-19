package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public abstract class RotatingChair extends Chair {
    public RotatingChair(Player player, Block block, Location location) {
        super(player, block, location);
    }

    public RotatingChair(Player player, Block block) {
        super(player, block);
    }

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotate.EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }
}
