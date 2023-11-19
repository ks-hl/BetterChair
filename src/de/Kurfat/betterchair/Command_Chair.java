package de.kurfat.betterchair;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class Command_Chair implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You are not a player!");
            return true;
        }
        UUID uuid = player.getUniqueId();
        HashMap<UUID, Boolean> users = BetterChair.USERS;
        boolean enable = !(users.getOrDefault(uuid, true));
        users.put(uuid, enable);
        player.sendMessage(BetterChair.SETTINGS.getMessage(enable));
        return true;
    }

}
