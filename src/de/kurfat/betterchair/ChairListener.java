package de.kurfat.betterchair;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.UUID;

public class ChairListener implements Listener {
    private final BetterChair plugin;

    public ChairListener(BetterChair plugin) {
        this.plugin = plugin;
    }

    // REMOVE PASSENGERS FROM ARMOR_STAND
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        remove(get(event.getPlayer().getUniqueId()));
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        remove(get(event.getBlock()));
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        remove(get(event.getBlock()));
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        remove(get(player.getUniqueId()));
    }

    // CALL CHAIR EVENT
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        remove(get(player.getUniqueId()));
    }

    // CHANGE LOCATION TO SAVEPOINT AND REMOVE ARMOR_STAND
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        remove(get(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void on(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (Chair.isChair(entity)) entity.remove();
        }
    }

    @EventHandler
    public void on(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (Chair.isChair(entity)) entity.remove();
        }
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        if (!Chair.isChair(e.getRightClicked())) return;
        if (e.getRightClicked().getPassengers().isEmpty()) e.getRightClicked().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(EntityMountEvent e) {
        System.out.println("Mount");
        if (e.getMount().getType() != EntityType.ARMOR_STAND) return;
//        if (!Chair.isChair(e.getMount())) return;
        System.out.println("chair");
        if (e.getEntityType() != EntityType.PLAYER) return;
        System.out.println("player");
        e.setCancelled(false);
    }

    private Chair get(UUID key) {
        return plugin.getUUIDChairMap().get(key);
    }

    private Chair get(Block block) {
        return plugin.getBlockChairMap().get(block);
    }

    private void remove(Chair chair) {
        if (chair != null) chair.remove();
    }
}
