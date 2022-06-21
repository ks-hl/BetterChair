package de.Kurfat.Java.Minecraft.BetterChair;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChairException extends Exception {

    private Player player;
    private Block block;

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
