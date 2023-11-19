package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerChairEvent extends PlayerEvent {

    private final Chair chair;

    protected PlayerChairEvent(Player player, Chair chair) {
        super(player);
        this.chair = chair;
    }

    public Chair getChair() {
        return chair;
    }

}
