package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerChairSwitchEvent extends PlayerChairEvent {

    public static HandlerList HANDLERS = new HandlerList();

    private final boolean enable;

    public PlayerChairSwitchEvent(Player player, Chair chair, boolean enable) {
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
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
