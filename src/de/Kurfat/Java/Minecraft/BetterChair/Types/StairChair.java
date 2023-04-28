package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Player;

public class StairChair extends Chair {

    private final Stairs stairs;

    public StairChair(Player player, Block block) throws Exception {
        super(player, block);
        this.savepoint = player.getLocation().clone();
        if (!(block.getBlockData() instanceof Stairs))
            throw new Exception("This is not stairs: " + block);
        this.stairs = (Stairs) block.getBlockData();
        if (stairs.getHalf() != Half.BOTTOM) throw new Exception("Stairs half is not bottom: " + block);
        if (stairs.getShape() != Shape.STRAIGHT)
            throw new Exception("Stairs shape is not straight: " + block);
        double x = block.getX() + 0.5;
        double y = block.getY() - 1.15;
        double z = block.getZ() + 0.5;
        float yaw = 0F;
        float pitch = 0F;
        float face = 0.2F;
        if (this.stairs.getFacing() == BlockFace.WEST) {
            x += face;
            yaw = -90F;
        } else if (this.stairs.getFacing() == BlockFace.NORTH) {
            z += face;
            yaw = 00F;
        } else if (this.stairs.getFacing() == BlockFace.EAST) {
            x -= face;
            yaw = 90F;
        } else if (this.stairs.getFacing() == BlockFace.SOUTH) {
            z -= face;
            yaw = 180F;
        } else throw new Exception("Stairs facing not include: " + block);
        this.location = new Location(block.getWorld(), x, y, z, yaw, pitch);
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
        return ChairType.STAIR;
    }

    public Stairs getStairs() {
        return stairs;
    }

}
