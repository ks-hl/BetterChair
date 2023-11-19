package de.kurfat.betterchair;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import de.kurfat.betterchair.types.Chair;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WorldGuardAddon {

    private final WorldGuard worldGuard;
    private final WorldGuardPlugin plugin;
    private final HashMap<BetterChair.ChairType, StateFlag> flags = new HashMap<>();

    public WorldGuardAddon() {
        worldGuard = WorldGuard.getInstance();
        plugin = WorldGuardPlugin.inst();
        FlagRegistry register = WorldGuard.getInstance().getFlagRegistry();
        for (BetterChair.ChairType type : BetterChair.ChairType.values()) {
            StateFlag flag = new StateFlag("chair-" + type.name().toLowerCase(), true, RegionGroup.ALL);
            flags.put(type, flag);
            register.register(flag);
        }
    }

    public boolean check(Player player, StateFlag flag) {
        for (RegionManager manager : worldGuard.getPlatform().getRegionContainer().getLoaded())
            if (manager.getName().equals(player.getWorld().getName())) {
                ApplicableRegionSet regions = manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                LocalPlayer localP = plugin.wrapPlayer(player);
                return regions.queryState(localP, flag) == State.ALLOW;
            }
        return true;
    }

    public boolean check(Player player, Chair chair) {
        return check(player, flags.get(chair.getType()));
    }

}
