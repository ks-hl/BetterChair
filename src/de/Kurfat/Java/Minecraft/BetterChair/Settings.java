package de.Kurfat.Java.Minecraft.BetterChair;

import de.Kurfat.Java.Minecraft.BetterChair.BetterChair.ChairType;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class Settings {

    protected HashMap<ChairType, Boolean> global = new HashMap<ChairType, Boolean>();
    protected String message = "Chairs used: %value%";

    public String getMessage(boolean enable) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("%value%", enable ? ChatColor.GREEN + "ON" + ChatColor.RESET : ChatColor.DARK_RED + "OFF" + ChatColor.RESET));
    }

    public HashMap<ChairType, Boolean> getGlobal() {
        return global;
    }

}
