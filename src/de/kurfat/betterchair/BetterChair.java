package de.kurfat.betterchair;

import de.kurfat.betterchair.events.PlayerSitEvent;
import de.kurfat.betterchair.types.AnyChair;
import de.kurfat.betterchair.types.Chair;
import de.kurfat.betterchair.types.ChairType;
import de.kurfat.betterchair.types.RotatingChair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BetterChair extends JavaPlugin implements Listener, CommandExecutor {
    private final Map<UUID, LinkedList<Location>> locationHistoryMap = new HashMap<>();
    private static BetterChair instance;
    private boolean isStarted = false;
    private File usersFile;
    private WorldGuardAddon worldGuard;
    private final Map<UUID, Chair> uuidChairMap = new HashMap<>();
    private final Map<Block, Chair> blockChairMap = new HashMap<>();
    private Set<UUID> disabledUsers;

    public static BetterChair getInstance() {
        return instance;
    }

    public static void print(String message, Throwable t) {
        getInstance().getLogger().log(Level.WARNING, message + ": " + t.getMessage(), t);
    }

    public static WorldGuardAddon getWorldGuard() {
        return instance.worldGuard;
    }

    public static void info(String message) {
        getInstance().getLogger().info(message);
    }

    public static void warn(String message) {
        getInstance().getLogger().warning(message);
    }

    public static void error(String message) {
        getInstance().getLogger().severe(message);
    }

    public void createChair(Player player, Block block) {
        Chair currentChair = getBlockChairMap().get(block);
        if (currentChair != null && !currentChair.isRemoved()) return;
        for (ChairType chairType : ChairType.values()) {
            Chair chair;
            try {
                chair = chairType.getChairConstructor().create(player, block);
            } catch (IllegalArgumentException e) {
                continue;
            }
            PlayerSitEvent customEvent = new PlayerSitEvent(player, chair);
            Bukkit.getPluginManager().callEvent(customEvent);
            if (customEvent.isCancelled()) return;
            if (getWorldGuard() != null && !getWorldGuard().check(player, chair)) return;
            if (!chair.spawn()) {
                chair.remove();
                return;
            }
            Chair oldChair = uuidChairMap.put(player.getUniqueId(), chair);
            if (oldChair != null) oldChair.remove();
            blockChairMap.put(block, chair);
            return;
        }
        if (player.hasPermission("betterchair.sitanywhere") && block.getBoundingBox().getVolume() < 1) {
            Chair chair = new AnyChair(player, block);
            PlayerSitEvent customEvent = new PlayerSitEvent(player, chair);
            Bukkit.getPluginManager().callEvent(customEvent);
            if (customEvent.isCancelled()) return;
            if (!chair.spawn()) {
                chair.remove();
                return;
            }
            Chair oldChair = uuidChairMap.put(player.getUniqueId(), chair);
            if (oldChair != null) oldChair.remove();
            blockChairMap.put(block, chair);
        }
    }

    @Override
    public void onLoad() {
        try {
            if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) worldGuard = new WorldGuardAddon();
        } catch (Throwable t) {
            getLogger().warning("Failed to initialize WorldGuard flags!");
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        // LOAD FILES
        String path = getDataFolder().getAbsolutePath();

        try (Scanner scanner = new Scanner(usersFile = new File(path + "/users.json"))) {
            disabledUsers = new JSONObject(scanner.nextLine()).getJSONArray("disabled").toList().stream().map(o -> UUID.fromString(o.toString())).collect(Collectors.toSet());
            info("Users was loaded.");
        } catch (FileNotFoundException e) {
            disabledUsers = new HashSet<>();
            warn("Users not found. A new one is created.");
        } catch (Exception e) {
            error("Users could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
            return;
        }

        isStarted = true;

        // SAVE FILES
        try {
            save();
        } catch (IOException ex) {
            print("Error while saving", ex);
        }

        // START
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Chair chair : getBlockChairMap().values()) {
                if (chair.isRemoved()) continue;
                if (!(chair instanceof RotatingChair rotatingChair)) continue;
                rotatingChair.rotate(chair.getPlayer().getLocation().getYaw());
            }
        }, 0, 1);
        // START
        getServer().getScheduler().runTaskTimer(this, () -> {
            Set<UUID> online = new HashSet<>();
            for (Player player : getServer().getOnlinePlayers()) {
                online.add(player.getUniqueId());
                LinkedList<Location> locationHistory = locationHistoryMap.computeIfAbsent(player.getUniqueId(), u -> new LinkedList<>());
                if (!locationHistory.isEmpty()) {
                    Location last = locationHistory.getLast();
                    if (!Objects.equals(last.getWorld(), player.getWorld())) locationHistory.clear();
                }
                locationHistory.addLast(player.getLocation().clone());
                while (locationHistory.size() > 3) locationHistory.removeFirst();
            }
            locationHistoryMap.keySet().removeIf(uuid -> !online.contains(uuid));
        }, 0, 3);
        // START
        getServer().getScheduler().runTaskTimer(this, () -> {
            Set<Chair> remove = new HashSet<>();
            for (Chair chair : blockChairMap.values()) {
                if (!chair.getPlayer().isOnline() || !chair.hasPassengers()) remove.add(chair);
            }
            remove.forEach(Chair::remove);
            blockChairMap.values().removeIf(Chair::isRemoved);
            uuidChairMap.values().removeIf(Chair::isRemoved);
        }, 0, 100);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new ChairListener(this), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("chair")).setExecutor(this);
    }

    private double getAverageSpeed(UUID uuid) {
        LinkedList<Location> locationHistory = locationHistoryMap.get(uuid);
        if (locationHistory == null || locationHistory.size() < 2) return 0;
        Iterator<Location> it = locationHistory.iterator();
        Location last = null;
        double distances = 0;
        int count = 0;
        while (it.hasNext()) {
            Location current = it.next();
            if (last != null) {
                distances += current.distance(last);
                count++;
            }
            last = current;
        }
        return distances / count;
    }

    public void save() throws IOException {
        if (!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

        if (!usersFile.exists()) //noinspection ResultOfMethodCallIgnored
            usersFile.createNewFile();
        FileWriter writer = new FileWriter(usersFile);
        writer.write(new JSONObject().put("disabled", new JSONArray().putAll(disabledUsers)).toString());
        writer.flush();
        writer.close();
    }

    @Override
    public void onDisable() {
        if (!isStarted) return;
        new ArrayList<>(uuidChairMap.values()).forEach(Chair::remove);
        try {
            save();
        } catch (IOException ex) {
            print("Error while saving", ex);
        }
    }

    @Deprecated
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isOnGround() || disabledUsers.contains(event.getPlayer().getUniqueId()))
            return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        assert block != null;
        RayTraceResult rayTraceResult = player.rayTraceBlocks(3);
        if (rayTraceResult == null) return;
        Block hitBlock = rayTraceResult.getHitBlock();
        if (!block.equals(hitBlock)) return; // Ensures you can't click blocks on the other side of walls
        if (block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2
                || getBlockChairMap().containsKey(block)
                || getUUIDChairMap().containsKey(player.getUniqueId())
                || !block.getRelative(BlockFace.UP).isPassable())
            return;
        if (getAverageSpeed(player.getUniqueId()) > 1) {
            player.sendMessage("§c§oIt's hard to sit when you're moving so fast!");
            return;
        }

        createChair(player, block);
    }

    public void remove(Chair chair) {
        uuidChairMap.remove(chair.getPlayer().getUniqueId());
        blockChairMap.remove(chair.getBlock());
    }

    Map<UUID, Chair> getUUIDChairMap() {
        return Collections.unmodifiableMap(uuidChairMap);
    }

    Map<Block, Chair> getBlockChairMap() {
        return Collections.unmodifiableMap(blockChairMap);
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You are not a player!");
            return true;
        }
        UUID uuid = player.getUniqueId();
        boolean enable = disabledUsers.contains(uuid);
        if (enable) disabledUsers.remove(uuid);
        else disabledUsers.add(uuid);
        player.sendMessage("§aSitting " + (enable ? "enabled" : "disabled") + ".");
        return true;
    }
}
