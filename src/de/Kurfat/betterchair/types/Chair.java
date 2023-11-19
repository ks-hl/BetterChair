package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class Chair {
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
        this.armorStand = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, CONSUMER);
        this.armorStand.addPassenger(player);
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
        BetterChair.getInstance().remove(this);
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

    public final boolean isRemoved() {
        return removed;
    }

    public final boolean hasPassengers() {
        return !armorStand.getPassengers().isEmpty();
    }
}
