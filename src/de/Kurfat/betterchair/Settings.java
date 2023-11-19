package de.kurfat.betterchair;

import org.bukkit.ChatColor;

import java.util.HashMap;

public class Settings {

    protected HashMap<BetterChair.ChairType, Boolean> global = new HashMap<>();
    protected final String message = "Chairs used: %value%";

    public String getMessage(boolean enable) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("%value%", enable ? ChatColor.GREEN + "ON" + ChatColor.RESET : ChatColor.DARK_RED + "OFF" + ChatColor.RESET));
    }

    public HashMap<BetterChair.ChairType, Boolean> getGlobal() {
        return global;
    }

}
