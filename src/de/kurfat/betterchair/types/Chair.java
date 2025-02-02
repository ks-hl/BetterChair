package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import de.kurfat.betterchair.events.PlayerStandEvent;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Chair {
    public static final String CHAIR_NBT = "BetterChair:IsChair";
    private static final Consumer<ArmorStand> CONSUMER = t -> {
        t.setVisible(false);
        t.setGravity(false);
        t.setArms(false);
        t.setBasePlate(false);
        t.setCollidable(false);
    };
    public static final Function<Double, Vector> OFFSETS = yOffset -> new Vector(0.5, -0.9 + yOffset, 0.5);
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

    public boolean spawn() {
        if (location.getWorld() == null) return false;
        this.armorStand = location.getWorld().spawn(location, ArmorStand.class, CONSUMER);

        NBT.modify(armorStand, nbt -> {
            nbt.setBoolean(CHAIR_NBT, true);
        });

        System.out.println("IsChair: " + isChair(armorStand));
        return this.armorStand.addPassenger(player);
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
        Bukkit.getServer().getPluginManager().callEvent(new PlayerStandEvent(player, this));
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
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

    public static boolean isChair(Entity entity) {
        return NBT.get(entity, nbt -> {
            return Objects.equals(nbt.getBoolean(CHAIR_NBT), true);
        });
    }
}
