package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SlapChair extends Chair {

    private final Slab slab;

    public SlapChair(Player player, Block block) throws Exception {
        super(player, block);
        if (!(block.getBlockData() instanceof Slab)) throw new Exception("This is not slap: " + block);
        this.slab = (Slab) block.getBlockData();
        if (slab.getType() == Type.BOTTOM) location = block.getLocation().clone().add(0.5, -1.20, 0.5);
        else location = block.getLocation().clone().add(0.5, -0.7, 0.5);
    }

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }

    @Override
    public ChairType getType() {
        return ChairType.SLAP;
    }

    public Slab getSlab() {
        return slab;
    }
}
