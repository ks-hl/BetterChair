package de.Kurfat.Java.Minecraft.BetterChair.Types;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AnyChair extends Chair {

	public AnyChair(Player player, Block block) {
		super(player, block, block.getLocation().clone().add(0.5, -0.7, 0.5));
	}

	@Override
	public ChairType getType() {
		return ChairType.BLOCK;
	}
	
	@EventHandler
	public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
		if(event.getEntity().equals(player)) armorStand.setRotation(event.getNewLocation().getYaw(), 0);
	}

}
