package de.kurfat.betterchair.types;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class StairChair extends Chair {

    public StairChair(Player player, Block block) throws Exception {
        super(ChairType.STAIR, player, block, null);
        this.savepoint = player.getLocation().clone();
        if (!(block.getBlockData() instanceof Stairs stairs))
            throw new Exception("This is not stairs: " + block);
        if (stairs.getHalf() != Half.BOTTOM) throw new Exception("Stairs half is not bottom: " + block);
        if (stairs.getShape() != Shape.STRAIGHT)
            throw new Exception("Stairs shape is not straight: " + block);

        Vector baseOffset = OFFSETS.apply(-0.5D);
        double x = block.getX() + baseOffset.getX();
        double y = block.getY() + baseOffset.getY();
        double z = block.getZ() + baseOffset.getZ();
        float yaw;
        float pitch = 0F;
        float face = 0.2F;
        if (stairs.getFacing() == BlockFace.WEST) {
            x += face;
            yaw = -90F;
        } else if (stairs.getFacing() == BlockFace.NORTH) {
            z += face;
            yaw = 00F;
        } else if (stairs.getFacing() == BlockFace.EAST) {
            x -= face;
            yaw = 90F;
        } else if (stairs.getFacing() == BlockFace.SOUTH) {
            z -= face;
            yaw = 180F;
        } else throw new Exception("Stairs facing not include: " + block);
        this.location = new Location(block.getWorld(), x, y, z, yaw, pitch);
    }
}
