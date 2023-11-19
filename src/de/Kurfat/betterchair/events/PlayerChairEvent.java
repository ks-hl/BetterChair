package de.kurfat.betterchair.events;

import de.kurfat.betterchair.types.IChair;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerChairEvent extends PlayerEvent {

    private final IChair chair;

    protected PlayerChairEvent(Player player, IChair chair) {
        super(player);
        this.chair = chair;
    }

    public IChair getChair() {
        return chair;
    }

}
