package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import de.Kurfat.Java.Minecraft.BetterChair.Chair.ChairBlockedException;
import de.Kurfat.Java.Minecraft.BetterChair.Chair.ChairParseException;

public class WorldChairs implements Listener{

	private World world;
	private HashMap<Block, Chair> chairs = new HashMap<Block, Chair>();
	
	public WorldChairs(World world) {
		this.world = world;
	}
	
	protected void disable() {
		for(Entry<Block, Chair> entry : chairs.entrySet()) if(entry.getValue().hasPlayer()){
			entry.getValue().disablePlayer();
			entry.getValue().disable();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| event.getHand() != EquipmentSlot.HAND
				|| event.hasItem()) return;
		Block block = event.getClickedBlock();
		if(block.getWorld().equals(world) == false || block.getLocation().clone().add(0.5, 0, 0.5).distance(event.getPlayer().getLocation()) > 2) return;
		Chair chair = null;
		if(chairs.containsKey(block)) {
			chair = chairs.get(block);
			if(chair.hasPlayer()) return;
		}
		else {
			try {
				chair = new Chair(block);
				chairs.put(block, chair);
				chair = chairs.get(block);
			} catch (ChairParseException e) {
				return;
			}
		}
		if(chair.hasPlayer() == false)
			try {
				chair.enablePlayer(event.getPlayer());
			} catch (ChairBlockedException e) {
				return;
			}
	}
	
	private void disableBlock(Block block) {
		if(block.getWorld().equals(world) == false || chairs.containsKey(block) == false) return;
		Chair chair = chairs.get(block);
		if(chair.hasPlayer()) chair.disablePlayer();
		chairs.remove(block);
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		disableBlock(event.getBlock());
	}
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		for(Block block : event.getBlocks()) disableBlock(block);
	}
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		for(Block block : event.getBlocks()) disableBlock(block);
	}
	
}
