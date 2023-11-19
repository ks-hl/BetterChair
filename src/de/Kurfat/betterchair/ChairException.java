package de.kurfat.betterchair;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChairException extends Exception {

    private final Player player;
    private final Block block;

    public ChairException(Player player, Block block, String message) {
        super(message);
        this.player = player;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

}
