package de.kurfat.betterchair.types;

import de.kurfat.betterchair.BetterChair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BlockChair extends RotatingChair {

    public static final List<String> BLOCKLIST = Arrays.asList(
            "\\w*_PRESSURE_PLATE",
            "\\w*_*LANTERN",
            "(\\w*_{0,1}AMETHYST_{0,1}\\w*)",
            "POINTED_DRIPSTONE",
            "DEAD_HORN_CORAL",
            "LIGHTNING_ROD",
            "\\w*_*GLASS_PANE"
    );

    public BlockChair(Player player, Block block) throws Exception {
        super(BetterChair.ChairType.BLOCK, player, block, block.getLocation().clone().add(0.5, -0.7, 0.5));
        Material bottom = block.getType();
        Material upper = block.getLocation().add(0, 1, 0).getBlock().getType();
        if (!bottom.isSolid() || bottom.isInteractable() || !upper.isAir())
            throw new Exception("This type is not allowed: " + block);
        for (String regex : BLOCKLIST)
            if (Pattern.matches("^(" + regex + ")$", block.getType().name()))
                throw new Exception("This type is not allowed: " + block);
    }
}
