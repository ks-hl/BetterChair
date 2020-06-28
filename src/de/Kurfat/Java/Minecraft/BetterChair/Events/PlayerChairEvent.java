package de.Kurfat.Java.Minecraft.BetterChair.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;

public abstract class PlayerChairEvent extends PlayerEvent{
	
	private IChair chair;
	
	protected PlayerChairEvent(Player player, IChair chair) {
		super(player);
		this.chair = chair;
	}

	public IChair getChair() {
		return chair;
	}
	
}
