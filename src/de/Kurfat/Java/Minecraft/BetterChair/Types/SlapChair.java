package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import de.Kurfat.Java.Minecraft.BetterChair.EntityPassengerRotate.EntityPassengerRotateEvent;
import de.Kurfat.Java.Minecraft.BetterChair.TypeParseException;
import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;

public class SlapChair extends Chair{

	private Slab slab;
	
	public SlapChair(Player player, Block block) throws TypeParseException {
		super(player, block);
		if(block.getBlockData() instanceof Slab == false) throw new TypeParseException("This is not slap: " + block.toString());
		this.slab = (Slab) block.getBlockData();
		if(slab.getType() == Type.BOTTOM) location = block.getLocation().clone().add(0.5, -1.20, 0.5);
		else location = block.getLocation().clone().add(0.5, -0.7, 0.5);
	}
	
	@Override
	public ChairType getType() {
		return ChairType.SLAP;
	}
	
	public Slab getSlab() {
		return slab;
	}

	@EventHandler
	public void onEntityPassengerRotate(EntityPassengerRotateEvent event) {
		if(event.getEntity().equals(player) == false) return;
		changeYawField(armorStand, event.getNewLocation().getYaw());
	}
}
