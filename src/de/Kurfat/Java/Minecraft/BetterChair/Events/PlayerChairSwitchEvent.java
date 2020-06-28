package de.Kurfat.Java.Minecraft.BetterChair.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;

public class PlayerChairSwitchEvent extends PlayerChairEvent {

	public static HandlerList HANDLERS = new HandlerList();
	
	private boolean enable;
	
	public PlayerChairSwitchEvent(Player player, IChair chair, boolean enable) {
		super(player, chair);
		this.enable = enable;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
