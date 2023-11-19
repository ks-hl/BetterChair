package de.kurfat.betterchair;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Command_Chair implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player!");
            return true;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        HashMap<UUID, Boolean> users = BetterChair.USERS;
        boolean enable = !(users.containsKey(uuid) ? users.get(uuid) : true);
        users.put(uuid, enable);
        player.sendMessage(BetterChair.SETTINGS.getMessage(enable));
        return true;
    }

}
