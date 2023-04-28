package de.Kurfat.Java.Minecraft.BetterChair.Events;

import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerChairCreateEvent extends PlayerChairEvent implements Cancellable {

    public static HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    public PlayerChairCreateEvent(Player player, IChair chair) {
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
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
