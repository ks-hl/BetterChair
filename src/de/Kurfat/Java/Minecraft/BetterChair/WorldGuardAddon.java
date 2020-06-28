package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.HashMap;

import org.bukkit.entity.Player;

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

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import de.Kurfat.Java.Minecraft.BetterChair.Types.IChair;

public class WorldGuardAddon {

	private WorldGuard worldGuard;
	private WorldGuardPlugin plugin;
	private HashMap<ChairType, StateFlag> flags = new HashMap<BetterChair.ChairType, StateFlag>();
	
	public WorldGuardAddon() {
		worldGuard = WorldGuard.getInstance();
		plugin = WorldGuardPlugin.inst();
		FlagRegistry register = WorldGuard.getInstance().getFlagRegistry();
		try {
			for(ChairType type : ChairType.values()) {
				StateFlag flag = new StateFlag("chair-" + type.name().toLowerCase(), true, RegionGroup.ALL);
				flags.put(type, flag);
				register.register(flag);
			}
		} catch (IllegalStateException e) {
			System.out.println("[BetterChair] WorldGuard may only start after BetterChair!");
		}
	}
	
	public boolean check(Player player, IChair chair) {
		for(RegionManager manager : worldGuard.getPlatform().getRegionContainer().getLoaded()) if(manager.getName().equals(player.getWorld().getName())){
			ApplicableRegionSet regions = manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
			LocalPlayer localP = plugin.wrapPlayer(player);
			StateFlag flag = flags.get(chair.getType());
			return regions.queryState(localP, flag) == State.ALLOW;
		}
		return true;
	}
	
}
