package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;

public class CarpetChair extends Chair {

	public CarpetChair(Player player, Block block) throws Exception {
		super(player, block, block.getLocation().clone().add(0.5, -1.6, 0.5));
		if(block.getType().name().endsWith("CARPET") == false) throw new Exception("This block is not carpet: " + block.toString());
	}
	
	@Override
	public ChairType getType() {
		return ChairType.CARPET;
	}
	
}
