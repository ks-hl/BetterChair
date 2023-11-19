package de.kurfat.betterchair.types;

import com.sk89q.worldguard.protection.flags.Flags;
import de.kurfat.betterchair.BetterChair;
import de.kurfat.betterchair.events.PlayerChairSwitchEvent;
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

public interface IChair extends Listener {

    void spawn();

    void remove();

    // REMOVE PASSENGERS FROM ARMOR_STAND
    @EventHandler(priority = EventPriority.MONITOR)
    default void onPlayerQuit(PlayerQuitEvent event) {
        Player player = getPlayer();
        if (event.getPlayer().equals(player)) remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    default void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().equals(getBlock())) remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    default void onBlockPhysics(BlockPhysicsEvent event) {
        if (!event.isCancelled() && event.getBlock().equals(getBlock())) remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    default void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !event.getEntity().equals(getPlayer())) return;
        if (BetterChair.WORLDGUARDADDON == null) return;
        if (!(event instanceof EntityDamageByEntityEvent event1)) return;

        Entity damager = event1.getDamager();
        if (BetterChair.WORLDGUARDADDON.check(getPlayer(), damager instanceof Player ? Flags.PVP : Flags.MOB_DAMAGE))
            remove();
    }

    // CALL CHAIR EVENT
    @EventHandler(priority = EventPriority.MONITOR)
    default void onEntityDismount(EntityDismountEvent event) {
        Player player = getPlayer();
        if (!event.isCancelled() && event.getEntity().equals(player)) {
            Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, this, false));
            remove();
        }
    }

    // CHANGE LOCATION TO SAVEPOINT AND REMOVE ARMOR_STAND
    @EventHandler(priority = EventPriority.HIGHEST)
    default void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = getPlayer();
        if (event.isCancelled() || !event.getPlayer().equals(player)) return;
        event.setTo(getSavePoint());
        remove();
    }

    // DEFAULT CHAIR METHODES
	Player getPlayer();

    Block getBlock();

    Location getLocation();

    Location getSavePoint();

    BetterChair.ChairType getType();

}
