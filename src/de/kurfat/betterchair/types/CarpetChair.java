package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CarpetChair extends RotatingChair {

    public CarpetChair(Player player, Block block) {
        super(ChairType.CARPET, player, block, block.getLocation().clone().add(Chair.OFFSETS.apply(-0.9D)));
        if (!block.getType().name().endsWith("CARPET"))
            throw new IllegalArgumentException("This block is not carpet: " + block);
    }
}
