package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CarpetChair extends RotatingChair {

    public CarpetChair(Player player, Block block) throws Exception {
        super(ChairType.CARPET, player, block, block.getLocation().clone().add(0.5, -1.6, 0.5));
        if (!block.getType().name().endsWith("CARPET"))
            throw new Exception("This block is not carpet: " + block);
    }
}
