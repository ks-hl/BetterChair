package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Player;

import de.Kurfat.Java.Minecraft.BetterChair.TypeParseException;

public class StairChair extends Chair{

	private Stairs stairs;
	
	public StairChair(Player player, Block block) throws TypeParseException {
		super(player, block);
		this.savepoint = player.getLocation().clone();
		if(block.getBlockData() instanceof Stairs == false) throw new TypeParseException("This is not stairs: " + block.toString());
		this.stairs = (Stairs) block.getBlockData();
		if(stairs.getHalf() != Half.BOTTOM) throw new TypeParseException("Stairs half is not bottom: " + block.toString());
		if(stairs.getShape() != Shape.STRAIGHT) throw new TypeParseException("Stairs shape is not straight: " + block.toString());
		double x = block.getX() + 0.5;
		double y = block.getY() - 1.15;
		double z = block.getZ() + 0.5;
		float yaw = 0F;
		float pitch = 0F;
		float face = 0.2F;
		if(this.stairs.getFacing() == BlockFace.WEST) {
			x += face;
			yaw = -90F;
		}
		else if(this.stairs.getFacing() == BlockFace.NORTH) {
			z += face;
			yaw = 00F;
		}
		else if(this.stairs.getFacing() == BlockFace.EAST) {
			x -= face;
			yaw = 90F;
		}
		else if(this.stairs.getFacing() == BlockFace.SOUTH) {
			z -= face;
			yaw = 180F;
		}
		else throw new TypeParseException("Stairs facing not include: " + block.toString());
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
	public Stairs getStairs() {
		return stairs;
	}
	
}
