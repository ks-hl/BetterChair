package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BedChair extends Chair {

    public static final long MIN_TIME_TICK = 12542;
    public static final long MAX_TIME_TICK = 23459;
    private final Part part;
    private final Block headBlock;
    private final Bed headBed;
    private final Block footBlock;
    private final Bed footBed;

    public BedChair(Player player, Block block) throws Exception {
        super(player, block);
        this.location = block.getLocation().clone().add(0.5, -1.15, 0.5);
        if (!(block.getBlockData() instanceof Bed)) throw new Exception("This is not bed: " + block);
        long worldticks = this.location.getWorld().getTime();
        if (worldticks >= MIN_TIME_TICK && worldticks <= MAX_TIME_TICK)
            throw new Exception("This world is in sleeping time: " + block);
        Bed bed1 = (Bed) block.getBlockData();
        part = bed1.getPart();
        Block block2 = block.getRelative(bed1.getPart() == Part.FOOT ? bed1.getFacing() : bed1.getFacing().getOppositeFace());
        Bed bed2 = (Bed) block2.getBlockData();
        if (bed1.getPart() == Part.HEAD) {
            this.headBlock = block;
            this.headBed = bed1;
            this.footBlock = block2;
            this.footBed = bed2;
        } else {
            this.headBlock = block2;
            this.headBed = bed2;
            this.footBlock = block;
            this.footBed = bed1;
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

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public ChairType getType() {
        return ChairType.BED;
    }

    public Part getPart() {
        return part;
    }

    public Block getHeadBlock() {
        return headBlock;
    }

    public Bed getHeadBed() {
        return headBed;
    }

    public Block getFootBlock() {
        return footBlock;
    }

    public Bed getFootBed() {
        return footBed;
    }

}
