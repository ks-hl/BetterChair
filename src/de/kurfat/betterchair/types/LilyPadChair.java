package de.kurfat.betterchair.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LilyPadChair extends RotatingChair {

    public LilyPadChair(Player player, Block block) {
        super(ChairType.LILY_PAD, player, block, block.getLocation().clone().add(Chair.OFFSETS.apply(-1D)));
        if (block.getType() != Material.LILY_PAD) {
            throw new IllegalArgumentException("This block is not a lily pad: " + block);
        }
    }
}
