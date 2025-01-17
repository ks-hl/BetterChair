package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;

public class SlabChair extends RotatingChair {

    public SlabChair(Player player, Block block) throws Exception {
        super(ChairType.SLAB, player, block);
        if (!(block.getBlockData() instanceof Slab slab)) throw new Exception("This is not slab: " + block);
        if (slab.getType() == Type.BOTTOM) location = block.getLocation().clone().add(0.5, -1.20, 0.5);
        else location = block.getLocation().clone().add(0.5, -0.7, 0.5);
    }
}
