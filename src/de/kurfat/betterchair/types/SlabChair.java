package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;

public class SlabChair extends RotatingChair {

    public SlabChair(Player player, Block block) {
        super(ChairType.SLAB, player, block, block.getLocation().clone().add(OFFSETS.apply(getOffset(block))));
    }

    private static double getOffset(Block block) {
        if (!(block.getBlockData() instanceof Slab slab)) throw new IllegalArgumentException("This is not slab: " + block);
        return slab.getType() == Type.BOTTOM ? -0.5 : 0;
    }
}
