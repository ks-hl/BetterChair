package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerChairEvent extends Event {

    public static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Chair chair;

    public PlayerChairEvent(Player player, Chair chair) {
        this.player = player;
        this.chair = chair;
    }

    public final Player getPlayer() {
        return player;
    }

    public final Chair getChair() {
        return chair;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
