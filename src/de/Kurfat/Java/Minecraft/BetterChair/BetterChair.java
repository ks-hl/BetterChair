package de.Kurfat.Java.Minecraft.BetterChair;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import de.Kurfat.Java.Minecraft.BetterChair.Types.BedChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.BlockChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.CarpetChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.Chair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SlapChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.SnowChair;
import de.Kurfat.Java.Minecraft.BetterChair.Types.StairChair;

public class BetterChair extends JavaPlugin implements Listener{

	public static BetterChair INSTANCE;
	
	public BetterChair() {}
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		EntityPassengerRotate.INSTANCE.start();
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Listener)this);
		EntityPassengerRotate.INSTANCE.stop();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND 
				|| event.hasItem() 
				|| event.getAction() != Action.RIGHT_CLICK_BLOCK 
				|| event.getPlayer().isOnGround() == false) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2 
				|| Chair.CACHE_BY_BLOCK.containsKey(block)
				|| Chair.CACHE_BY_PLAYER.containsKey(player)
				|| block.getRelative(BlockFace.UP).isPassable() == false) return;
		try {
			Chair chair = new StairChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
		try {
			Chair chair = new SlapChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
		try {
			Chair chair = new BedChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
		try {
			Chair chair = new SnowChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
		try {
			Chair chair = new CarpetChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
		try {
			Chair chair = new BlockChair(player, block);
			chair.spawn();
			return;
		} catch (TypeParseException e) {}
	}
	
}
