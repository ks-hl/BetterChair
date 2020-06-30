package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.HashMap;

import org.bukkit.ChatColor;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;

public class Settings {

	protected HashMap<ChairType, Boolean> global = new HashMap<ChairType, Boolean>();
	protected String message = "Chairs used: %value%";
	
	public HashMap<ChairType, Boolean> getGlobal() {
		return global;
	}
	
	public String getMessage(boolean enable) {
		return ChatColor.translateAlternateColorCodes('&', message.replace("%value%", enable ? ChatColor.GREEN + "ON" + ChatColor.RESET : ChatColor.DARK_RED + "OFF" + ChatColor.RESET));
	}
	
}
