package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Chair implements IChair {

    public static final HashMap<Player, Chair> CACHE_BY_PLAYER = new HashMap<>();
    public static final HashMap<Block, Chair> CACHE_BY_BLOCK = new HashMap<>();
    private static final Consumer<ArmorStand> CONSUMER = t -> {
        t.setVisible(false);
        t.setGravity(false);
        t.setArms(false);
        t.setBasePlate(false);
        t.setCollidable(false);
    };

    protected Player player;
    protected Block block;
    private final BetterChair.ChairType type;
    protected Location location;
    protected Location savepoint;
    protected ArmorStand armorStand;
    private boolean removed;

    public Chair(BetterChair.ChairType type, Player player, Block block, Location location) {
        this(type, player, block);
        this.location = location;
    }

    protected Chair(BetterChair.ChairType type, Player player, Block block) {
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

    public Location getLocation() {
        return location;
    }

    public Location getSavePoint() {
        return savepoint;
    }

    @Override
    public final BetterChair.ChairType getType() {
        return type;
    }
}
