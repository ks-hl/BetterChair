package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import de.Kurfat.Java.Minecraft.BetterChair.TypeParseException;
import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;

public class BlockChair extends Chair{
	
	public BlockChair(Player player, Block block) throws TypeParseException {
		super(player, block, block.getLocation().clone().add(0.5, -0.7, 0.5));
		Material bottom = block.getType();
		Material upper = block.getLocation().add(0, 1, 0).getBlock().getType();
		if(bottom.isSolid() == false || bottom.isInteractable() || upper.isAir() == false) throw new TypeParseException("This type is not allowed: " + block.toString());
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
