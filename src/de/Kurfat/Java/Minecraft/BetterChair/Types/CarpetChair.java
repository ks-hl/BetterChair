package de.Kurfat.Java.Minecraft.BetterChair.Types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.Kurfat.Java.Minecraft.BetterChair.TypeParseException;

public class CarpetChair extends Chair {

	public CarpetChair(Player player, Block block) throws TypeParseException {
		super(player, block, block.getLocation().clone().add(0.5, -1.6, 0.5));
		if(block.getType().name().endsWith("CARPET") == false) throw new TypeParseException("This block is not carpet: " + block.toString());
	}
	
}
