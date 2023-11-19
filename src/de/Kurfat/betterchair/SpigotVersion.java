package de.kurfat.betterchair;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotVersion {

    private static final String REGEX = "^(1\\.(\\d{1,2})(\\.(\\d{1,2})){0,1})\\S+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    public static int current() {
        Matcher matcher = PATTERN.matcher(Bukkit.getBukkitVersion());
        return matcher.find() ? Integer.parseInt(matcher.group(2)) : -1;
    }

}
