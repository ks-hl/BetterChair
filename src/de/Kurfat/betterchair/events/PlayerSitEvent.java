package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerSitEvent extends PlayerChairEvent implements Cancellable {

    public static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    public PlayerSitEvent(Player player, Chair chair) {
        super(player, chair);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
