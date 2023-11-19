package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerStandEvent extends PlayerChairEvent {

    public static final HandlerList HANDLERS = new HandlerList();

    public PlayerStandEvent(Player player, Chair chair) {
        super(player, chair);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
