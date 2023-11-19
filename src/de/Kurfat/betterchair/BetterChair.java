package de.kurfat.betterchair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import de.kurfat.betterchair.events.PlayerChairCreateEvent;
import de.kurfat.betterchair.events.PlayerChairSwitchEvent;
import de.kurfat.betterchair.types.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class BetterChair extends JavaPlugin implements Listener {

    public static BetterChair INSTANCE;
    public static WorldGuardAddon WORLDGUARDADDON;
    protected static Settings SETTINGS;
    protected static HashMap<UUID, Boolean> USERS;
    private static boolean IS_STARTED = false;
    private static File SETTINGS_FILE;
    private static File USERS_FILE;
    private static final LinkedTreeMap<ChairType, Class<? extends IChair>> BUILDERS = new LinkedTreeMap<>();

    public BetterChair() {
        BUILDERS.put(ChairType.STAIR, StairChair.class);
        BUILDERS.put(ChairType.SLAB, SlabChair.class);
        BUILDERS.put(ChairType.BED, BedChair.class);
        BUILDERS.put(ChairType.SNOW, SnowChair.class);
        BUILDERS.put(ChairType.CARPET, CarpetChair.class);
        BUILDERS.put(ChairType.BLOCK, BlockChair.class);
    }

    public static void createChair(Player player, Block block) {
        for (Entry<ChairType, Class<? extends IChair>> builder : BUILDERS.entrySet())
            if (SETTINGS.getGlobal().get(builder.getKey())) {
                IChair chair;
                try {
                    chair = builder.getValue().getConstructor(Player.class, Block.class).newInstance(player, block);
                } catch (Exception e) {
                    continue;
                }
                PlayerChairCreateEvent customEvent = new PlayerChairCreateEvent(player, chair);
                Bukkit.getPluginManager().callEvent(customEvent);
                if (customEvent.isCancelled()) return;
                if (WORLDGUARDADDON != null && !WORLDGUARDADDON.check(player, chair)) return;
                chair.spawn();
                Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, chair, true));
                return;
            }
        if (player.hasPermission("betterchair.sitanywhere") && block.getBoundingBox().getVolume() < 1) {
            IChair chair = new AnyChair(player, block);
            chair.spawn();
            Bukkit.getPluginManager().callEvent(new PlayerChairSwitchEvent(player, chair, true));
        }
    }

    private static void message(ChatColor color, String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "BetterChair" + ChatColor.DARK_GRAY + "]" + color + " " + message + ChatColor.RESET);
    }

    private static void info(String message) {
        message(ChatColor.GREEN, message);
    }

    private static void warn(String message) {
        message(ChatColor.YELLOW, message);
    }

    private static void error(String message) {
        message(ChatColor.RED, message);
    }

    @Override
    public void onLoad() {
        try {
            if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) WORLDGUARDADDON = new WorldGuardAddon();
        } catch (Throwable t) {
            getLogger().warning("Failed to initialize WorldGuard flags!");
        }
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        // LOAD FILES
        String path = getDataFolder().getAbsolutePath();
        Gson gson = new GsonBuilder().create();
        try {
            SETTINGS_FILE = new File(path + "/settings.json");
            SETTINGS = gson.fromJson(new FileReader(SETTINGS_FILE), Settings.class);
            if (SETTINGS.global == null || SETTINGS.global.isEmpty() || SETTINGS.message == null) {
                error("Settings could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
                return;
            }
            for (ChairType type : ChairType.values())
                if (!SETTINGS.global.containsKey(type)) {
                    SETTINGS.global.put(type, true);
                    warn("The chair \"" + type + "\" was not set in the settings and now set to \"true\"!");
                }
            info("Settings was loaded.");
        } catch (FileNotFoundException e) {
            SETTINGS = new Settings();
            SETTINGS.global = new HashMap<>();
            for (ChairType type : ChairType.values()) SETTINGS.global.put(type, true);
            warn("Settings not found. A new one is created.");
        } catch (Exception e) {
            error("Settings could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
            return;
        }
        try {
            USERS_FILE = new File(path + "/users.json");
            USERS = gson.fromJson(new FileReader(USERS_FILE), new TypeToken<HashMap<UUID, Boolean>>() {
            }.getType());
            info("Users was loaded.");
        } catch (FileNotFoundException e) {
            USERS = new HashMap<UUID, Boolean>();
            warn("Users not found. A new one is created.");
        } catch (Exception e) {
            error("Users could not be loaded. Please check your settings. If you need help you can reach me via Spigot @Kurfat.");
            return;
        }

        IS_STARTED = true;

        // SAVE FILES
        try {
            save();
        } catch (IOException ex) {
            print("Error while saving", ex);
        }

        // START
        EntityPassengerRotate.INSTANCE.start();
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(Bukkit.getPluginCommand("chair")).setExecutor(new Command_Chair());
    }

    public void save() throws IOException {
        if (!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

        if (!SETTINGS_FILE.exists()) //noinspection ResultOfMethodCallIgnored
            SETTINGS_FILE.createNewFile();
        FileWriter writer = new FileWriter(SETTINGS_FILE);
        writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(SETTINGS));
        writer.flush();
        writer.close();

        if (!USERS_FILE.exists()) //noinspection ResultOfMethodCallIgnored
            USERS_FILE.createNewFile();
        writer = new FileWriter(USERS_FILE);
        writer.write(new GsonBuilder().create().toJson(USERS));
        writer.flush();
        writer.close();
    }

    @Override
    public void onDisable() {
        if (!IS_STARTED) return;
        new ArrayList<>(Chair.CACHE_BY_PLAYER.values()).forEach(Chair::remove);
        HandlerList.unregisterAll((Listener) this);
        EntityPassengerRotate.INSTANCE.stop();
        try {
            save();
        } catch (IOException ex) {
            print("Error while saving", ex);
        }
    }

    @Deprecated
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || event.hasItem()
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || !event.getPlayer().isOnGround()
                || USERS.containsKey(event.getPlayer().getUniqueId()) && !USERS.get(event.getPlayer().getUniqueId()))
            return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        assert block != null;
        if (block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) > 2
                || Chair.CACHE_BY_BLOCK.containsKey(block)
                || Chair.CACHE_BY_PLAYER.containsKey(player)
                || !block.getRelative(BlockFace.UP).isPassable()) return;

        createChair(player, block);
    }

    public enum ChairType {
        STAIR, SLAB, BED, SNOW, CARPET, BLOCK
    }

    public void print(String message, Throwable t) {
        getLogger().log(Level.WARNING, message + ": " + t.getMessage(), t);
    }
}
