package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.Objects;

public class BedChair extends RotatingChair {

    public static final long MIN_TIME_TICK = 12542;
    public static final long MAX_TIME_TICK = 23459;
    private final Block headBlock;
    private final Block footBlock;

    public BedChair(Player player, Block block) throws Exception {
        super(BetterChair.ChairType.BED, player, block);
        this.location = block.getLocation().clone().add(0.5, -1.15, 0.5);
        if (!(block.getBlockData() instanceof Bed bed1)) throw new Exception("This is not bed: " + block);
        long worldTicks = Objects.requireNonNull(this.location.getWorld()).getTime();
        if (worldTicks >= MIN_TIME_TICK && worldTicks <= MAX_TIME_TICK)
            throw new Exception("This world is in sleeping time: " + block);
        Block block2 = block.getRelative(bed1.getPart() == Part.FOOT ? bed1.getFacing() : bed1.getFacing().getOppositeFace());
        if (bed1.getPart() == Part.HEAD) {
            this.headBlock = block;
            this.footBlock = block2;
        } else {
            this.headBlock = block2;
            this.footBlock = block;
        }
    }

    @EventHandler
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (headBlock.equals(block) || footBlock.equals(block)) remove();
    }

    @EventHandler
    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (headBlock.equals(block) || footBlock.equals(block)) remove();
    }
}
