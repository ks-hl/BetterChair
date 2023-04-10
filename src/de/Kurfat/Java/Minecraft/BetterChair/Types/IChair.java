package de.Kurfat.Java.Minecraft.BetterChair.Types;

import com.sk89q.worldguard.protection.flags.Flags;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
	public void remove();
	public ChairType getType();

	// REMOVE PASSENGERS FROM ARMOR_STAND
	@EventHandler(priority = EventPriority.MONITOR)
	default void onPlayerQuit(PlayerQuitEvent event) {
		Player player = getPlayer();
		if(event.getPlayer().equals(player)) remove();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	default void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled() == false && event.getBlock().equals(getBlock())) remove();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	default void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.isCancelled() == false && event.getBlock().equals(getBlock())) remove();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	default void onEntityDamage(EntityDamageEvent event) {
		if(event.isCancelled() || !event.getEntity().equals(getPlayer())) return;
		if(BetterChair.WORLDGUARDADDON == null) return;
		if(!(event instanceof EntityDamageByEntityEvent event1)) return;

		Entity damager = event1.getDamager();
		if(BetterChair.WORLDGUARDADDON.check(getPlayer(), damager instanceof Player ? Flags.PVP : Flags.MOB_DAMAGE)) remove();
	}

	// CALL CHAIR EVENT
	@EventHandler(priority = EventPriority.MONITOR)
	default void onEntityDismount(EntityDismountEvent event) {
		Player player = getPlayer();
		if(event.isCancelled() == false && event.getEntity().equals(player)) {
			Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, this, false));
			remove();
		}
	}

	// CHANGE LOCATION TO SAVEPOINT AND REMOVE ARMOR_STAND
	@EventHandler(priority = EventPriority.HIGHEST)
	default void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = getPlayer();
		if(event.isCancelled() || event.getPlayer().equals(player) == false) return;
		event.setTo(getSavePoint());
		remove();
	}

}
