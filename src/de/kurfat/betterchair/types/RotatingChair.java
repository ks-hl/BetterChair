package de.kurfat.betterchair.types;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public abstract class RotatingChair extends Chair {
    private float yaw = Float.MIN_VALUE;

    public RotatingChair(ChairType type, Player player, Block block, Location location) {
        super(type, player, block, location);
    }

    public void rotate(float yaw) {
        if (Math.abs(this.yaw - yaw) < 1E-3) return;
        armorStand.setRotation(this.yaw = yaw, 0);
    }
}
