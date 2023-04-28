package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SnowChair extends Chair {

    public SnowChair(Player player, Block block) throws Exception {
        super(player, block);
        if (!(block.getBlockData() instanceof Snow)) throw new Exception("This is not snow: " + block);
        Snow snow = (Snow) block.getBlockData();
        double one = 0.125;
        location = block.getLocation().clone().add(0.5, -1.7, 0.5);
        location.setY(location.getY() + one * snow.getLayers());
    }

    @EventHandler
    public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
        if (event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
    }

    @Override
    public ChairType getType() {
        return ChairType.SNOW;
    }
}
