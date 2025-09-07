package de.kurfat.betterchair.types;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ChairConstructor {
    Chair create(Player player, Block block) throws IllegalArgumentException;
}
