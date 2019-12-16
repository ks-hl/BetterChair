package de.Kurfat.Java.Minecraft.BetterChair.Settings;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.Kurfat.Java.Minecraft.BetterChair.SettingsParseExeotion;

public class CarpetChair extends Chair {

	public CarpetChair(Player player, Block block) throws SettingsParseExeotion {
		super(player, block, block.getLocation().clone().add(0.5, -1.6, 0.5));
		if(block.getType().name().endsWith("CARPET") == false) throw new SettingsParseExeotion("This block is not carpet: " + block.toString());
	}
	
}
