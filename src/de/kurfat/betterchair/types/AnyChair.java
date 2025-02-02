package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AnyChair extends RotatingChair {
    public AnyChair(Player player, Block block) {
        super(ChairType.BLOCK, player, block, block.getLocation().clone().add(0.5, -0.7, 0.5));
    }
}
