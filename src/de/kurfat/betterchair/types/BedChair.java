package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BedChair extends RotatingChair {

    public static final long MIN_TIME_TICK = 12542;
    public static final long MAX_TIME_TICK = 23459;

    public BedChair(Player player, Block block) throws Exception {
        super(ChairType.BED, player, block, block.getLocation().clone().add(Chair.OFFSETS.apply(-0.45D)));
        if (!(block.getBlockData() instanceof Bed)) throw new Exception("This is not bed: " + block);
        long worldTicks = Objects.requireNonNull(this.location.getWorld()).getTime();
        if (worldTicks >= MIN_TIME_TICK && worldTicks <= MAX_TIME_TICK)
            throw new Exception("This world is in sleeping time: " + block);
    }
}
