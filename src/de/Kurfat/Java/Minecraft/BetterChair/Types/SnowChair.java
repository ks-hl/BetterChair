package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import de.Kurfat.Java.Minecraft.BetterChair.TypeParseException;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;

public class SnowChair extends Chair{

	public SnowChair(Player player, Block block) throws TypeParseException {
		super(player, block);
		if(block.getBlockData() instanceof Snow == false) throw new TypeParseException("This is not snow: " + block.toString());
		Snow snow = (Snow) block.getBlockData();
		double one = 0.125;
		location = block.getLocation().clone().add(0.5, -1.7, 0.5);
		location.setY(location.getY() + one * snow.getLayers());
	}

	@EventHandler
	public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
		if(event.getEntity().equals(player) == false) return;
		changeYawField(armorStand, event.getNewLocation().getYaw());
	}
}
