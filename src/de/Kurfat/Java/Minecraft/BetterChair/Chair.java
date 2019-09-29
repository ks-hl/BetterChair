package de.Kurfat.Java.Minecraft.BetterChair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Consumer;

public class Chair implements Listener{

	private Block block;
	private Stairs stairs;
	private ArmorStand armorStand;
	private Player player;
	private Location savepoint;

	public Chair(Block block) throws ChairParseException {
		this.block = block;
		if(this.block.getBlockData() instanceof Stairs == false) throw new ChairParseException();
		stairs = (Stairs) this.block.getBlockData();
		if(stairs.getHalf() != Half.BOTTOM || stairs.getShape() != Shape.STRAIGHT) throw new ChairParseException();
	}
	public static class ChairParseException extends Exception{
		private static final long serialVersionUID = -2591052184824107874L;
		private ChairParseException() {}
	}
	public static class ChairBlockedException extends Exception{
		private static final long serialVersionUID = -2591052184824107874L;
		private ChairBlockedException() {}
	}
	
	public void enablePlayer(Player player) throws ChairBlockedException {
		if(player.isOnGround() == false || block.getRelative(BlockFace.UP).isPassable() == false) throw new ChairBlockedException();
		
		this.player = player;
		savepoint = player.getLocation().clone();
		
		double x = block.getX() + 0.5;
		double y = block.getY() - 1.15;
		double z = block.getZ() + 0.5;
		float yaw = 0F;
		float pitch = 0F;
		float face = 0.2F;
		if(stairs.getFacing() == BlockFace.WEST) {
			x += face;
			yaw = -90F;
		}
		else if(stairs.getFacing() == BlockFace.NORTH) {
			z += face;
			yaw = 00F;
		}
		else if(stairs.getFacing() == BlockFace.EAST) {
			x -= face;
			yaw = 90F;
		}
		if(stairs.getFacing() == BlockFace.SOUTH) {
			z -= face;
			yaw = 180F;
		}

		Location location = new Location(block.getWorld(), x, y, z, yaw, pitch);
		armorStand = location.getWorld().spawn(location, ArmorStand.class, new Consumer<ArmorStand>() {

			@Override
			public void accept(ArmorStand armorStand) {
				armorStand.setVisible(false);
				armorStand.setGravity(false);
				armorStand.setArms(false);
				armorStand.setBasePlate(false);
				armorStand.setCollidable(false);
				armorStand.setInvulnerable(true);
			}
		});
		armorStand.addPassenger(player);
		Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
	}
	public void disablePlayer() {
		player.teleport(savepoint);
	}
	public boolean hasPlayer() {
		return player != null;
	}
	protected void disable() {
		player = null;
		savepoint = null;
		armorStand.remove();
		armorStand = null;
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		event.setTo(savepoint);
		disable();
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		disablePlayer();
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity().equals(player) == false) return;
		disablePlayer();
	}
	
}
