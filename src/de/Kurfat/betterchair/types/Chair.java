package de.kurfat.betterchair.types;

import com.sk89q.worldguard.protection.flags.Flags;
import de.kurfat.betterchair.BetterChair;
import de.kurfat.betterchair.events.PlayerChairSwitchEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Chair implements Listener {

    public static final HashMap<Player, Chair> CACHE_BY_PLAYER = new HashMap<>();
    public static final HashMap<Block, Chair> CACHE_BY_BLOCK = new HashMap<>();
    private static final Consumer<ArmorStand> CONSUMER = t -> {
        t.setVisible(false);
        t.setGravity(false);
        t.setArms(false);
        t.setBasePlate(false);
        t.setCollidable(false);
    };
    private final ChairType type;
    protected final Player player;
    protected final Block block;
    protected Location location;
    protected Location savepoint;
    protected ArmorStand armorStand;
    private boolean removed;

    public Chair(ChairType type, Player player, Block block, Location location) {
        this(type, player, block);
        this.location = location;
    }

    protected Chair(ChairType type, Player player, Block block) {
        this.type = type;
        this.player = player;
        this.block = block;
        this.savepoint = player.getLocation().clone();
    }

    public void spawn() {
        CACHE_BY_PLAYER.put(player, this);
        CACHE_BY_BLOCK.put(block, this);
        this.armorStand = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, CONSUMER);
        this.armorStand.addPassenger(player);
        Bukkit.getPluginManager().registerEvents(this, BetterChair.INSTANCE);
    }

    public void remove() {
        if (removed) return;
        removed = true;
        if (player.isOnline()) {
            savepoint.setPitch(player.getLocation().getPitch());
            savepoint.setYaw(player.getLocation().getYaw());
            player.teleport(savepoint);
        }
        this.armorStand.remove();
        HandlerList.unregisterAll(this);
        CACHE_BY_BLOCK.remove(block);
        CACHE_BY_PLAYER.remove(player);
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public Location getSavePoint() {
        return savepoint;
    }

    public final ChairType getType() {
        return type;
    }

    // REMOVE PASSENGERS FROM ARMOR_STAND
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = getPlayer();
        if (event.getPlayer().equals(player)) remove();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().equals(getBlock())) remove();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!event.isCancelled() && event.getBlock().equals(getBlock())) remove();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !event.getEntity().equals(getPlayer())) return;
        if (BetterChair.WORLDGUARDADDON == null) return;
        if (!(event instanceof EntityDamageByEntityEvent event1)) return;

        Entity damager = event1.getDamager();
        if (BetterChair.WORLDGUARDADDON.check(getPlayer(), damager instanceof Player ? Flags.PVP : Flags.MOB_DAMAGE))
            remove();
    }

    // CALL CHAIR EVENT
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDismount(EntityDismountEvent event) {
        Player player = getPlayer();
        if (!event.isCancelled() && event.getEntity().equals(player)) {
            Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, this, false));
            remove();
        }
    }

    // CHANGE LOCATION TO SAVEPOINT AND REMOVE ARMOR_STAND
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = getPlayer();
        if (event.isCancelled() || !event.getPlayer().equals(player)) return;
        event.setTo(getSavePoint());
        remove();
    }
}
