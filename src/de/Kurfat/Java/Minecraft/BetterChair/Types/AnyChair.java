package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AnyChair extends Chair {

    public AnyChair(Player player, Block block) {
        super(player, block, block.getLocation().clone().add(0.5, -0.7, 0.5));
    }

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }

    @Override
    public ChairType getType() {
        return ChairType.BLOCK;
    }

}
