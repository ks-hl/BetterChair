package de.Kurfat.Java.Minecraft.BetterChair.Types;

import com.sk89q.worldguard.protection.flags.Flags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair;
import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.Events.PlayerChairSwitchEvent;

public interface IChair extends Listener {

	// DEFAULT CHAIR METHODES
	public Player getPlayer();
	public Block getBlock();
	public Location getLocation();
	public Location getSavePoint();
	public void spawn();
	public void eject();
	public void remove();
	public ChairType getType();
	
	// REMOVE PASSENGERS FROM ARMOR_STAND
	@EventHandler
	default void onPlayerQuit(PlayerQuitEvent event) {
		Player player = getPlayer();
		if(event.getPlayer().equals(player)) eject();
	}
	@EventHandler
	default void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().equals(getBlock())) eject();
	}
	@EventHandler
	default void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.getBlock().equals(getBlock())) eject();
	}
	@EventHandler
	default void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity().equals(getPlayer()) == false) return;
		else if(BetterChair.WORLDGUARDADDON == null || event instanceof EntityDamageByEntityEvent == false) eject();
		else {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			if(BetterChair.WORLDGUARDADDON.check(getPlayer(), damager instanceof Player ? Flags.PVP : Flags.MOB_DAMAGE)) eject();
		}
	}
	
	// CALL CHAIR EVENT
	@EventHandler
	default void onEntityDismount(EntityDismountEvent event) {
		Player player = getPlayer();
		if(event.getEntity().equals(player)) Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, this, false));
	}

	// CHANGE LOCATION TO SAVEPOINT AND REMOVE ARMOR_STAND
	@EventHandler
	default void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = getPlayer();
		if(event.getPlayer().equals(player) == false) return;
		event.setTo(getSavePoint());
		remove();
	}

}
