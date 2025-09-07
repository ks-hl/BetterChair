package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;

public class SnowChair extends RotatingChair {

    public SnowChair(Player player, Block block) {
        super(ChairType.SNOW, player, block, block.getLocation().clone().add(Chair.OFFSETS.apply(getOffset(block))));
    }

    private static double getOffset(Block block) {
        if (!(block.getBlockData() instanceof Snow snow)) throw new IllegalArgumentException("This is not snow: " + block);
        return 0.125 * snow.getLayers() - 1;
    }
}
