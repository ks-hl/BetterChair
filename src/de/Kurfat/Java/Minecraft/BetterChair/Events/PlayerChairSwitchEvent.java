package de.Kurfat.Java.Minecraft.BetterChair.Events;

import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerChairSwitchEvent extends PlayerChairEvent {

    public static HandlerList HANDLERS = new HandlerList();

    private final boolean enable;

    public PlayerChairSwitchEvent(Player player, IChair chair, boolean enable) {
        super(player, chair);
        this.enable = enable;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
