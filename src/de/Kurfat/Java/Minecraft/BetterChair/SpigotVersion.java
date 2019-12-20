package de.Kurfat.Java.Minecraft.BetterChair;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public enum SpigotVersion {

	VERSION_UNKNOWN,
	VERSION_1_14,
	VERSION_1_15;

	public static SpigotVersion currentVersion() {
		String version = Bukkit.getVersion();
		if(version.endsWith("(MC: 1.15)") 
				|| version.endsWith("(MC: 1.15.1)") 
				|| version.endsWith("(MC: 1.15.2)") 
				|| version.endsWith("(MC: 1.15.3)") 
				|| version.endsWith("(MC: 1.15.4)")) return VERSION_1_15;
		if(version.endsWith("(MC: 1.14)") 
				|| version.endsWith("(MC: 1.14.1)") 
				|| version.endsWith("(MC: 1.14.2)") 
				|| version.endsWith("(MC: 1.14.3)") 
				|| version.endsWith("(MC: 1.14.4)")) return VERSION_1_14;
		return VERSION_UNKNOWN;
	}
	
	public static List<SpigotVersion> compatibleVersions(){
		List<SpigotVersion> versions = new ArrayList<SpigotVersion>();
		SpigotVersion version = currentVersion();
		if(version == VERSION_UNKNOWN) return versions;
		versions.add(VERSION_1_14);
		if(version == VERSION_1_14) return versions;
		versions.add(VERSION_1_15);
		return versions;
	}

}
