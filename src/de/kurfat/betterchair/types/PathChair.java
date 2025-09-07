package de.kurfat.betterchair.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PathChair extends RotatingChair {

    public PathChair(Player player, Block block) {
        super(ChairType.PATH, player, block, block.getLocation().clone().add(Chair.OFFSETS.apply(-0.1D)));
        if (block.getType() != Material.DIRT_PATH) {
            throw new IllegalArgumentException("This block is not a path: " + block);
        }
    }
}
