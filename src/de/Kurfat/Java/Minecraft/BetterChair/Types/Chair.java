package de.Kurfat.Java.Minecraft.BetterChair.Types;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Consumer;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair;

public class Chair implements IChair{
	
	public static final HashMap<Player, Chair> CACHE_BY_PLAYER = new HashMap<Player, Chair>();
	public static final HashMap<Block, Chair> CACHE_BY_BLOCK = new HashMap<Block, Chair>();
	
	protected Player player;
	protected Block block;
	protected Location location;
	protected Location savepoint;
	protected ArmorStand armorStand;
	
	protected Chair(Player player, Block block) {
		this.player = player;
		this.block = block;
		this.savepoint = player.getLocation().clone();
	}
	public Chair(Player player, Block block, Location location) {
		this(player, block);
		this.location = location;
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
	public Location getSavePoint() {
		return savepoint;
	}
	
	public void spawn() {
		CACHE_BY_PLAYER.put(player, this);
		CACHE_BY_BLOCK.put(block, this);
		this.armorStand = location.getWorld().spawn(location, ArmorStand.class, new Consumer<ArmorStand>() {
			@Override
			public void accept(ArmorStand t) {
				t.setVisible(false);
				t.setGravity(false);
				t.setArms(false);
				t.setBasePlate(false);
				t.setCollidable(false);
			}
		});
		this.armorStand.addPassenger(player);
		Bukkit.getPluginManager().registerEvents(this, BetterChair.INSTANCE);
	}
	public void eject() {
		this.armorStand.eject();
	}
	public void remove() {
		this.armorStand.remove();
		HandlerList.unregisterAll(this);
		CACHE_BY_BLOCK.remove(block);
		CACHE_BY_PLAYER.remove(player);
	}
	
	protected boolean changeYawField(Entity entity, float yaw) {
		try {
			Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
		    nmsEntity.getClass().getField("yaw").set(nmsEntity, yaw);
		    return true;
		} catch (Exception e) {
			return false;
		}
	}
}
