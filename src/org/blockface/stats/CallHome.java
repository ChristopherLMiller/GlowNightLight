package org.blockface.stats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URL;
import java.util.UUID;

public class CallHome{
    private static final File file = new File("plugins/stats/config.yml");
    private static final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static void load(Plugin plugin) {
        if(!verifyConfig()) return;

        if(config.getBoolean("opt-out")) return;

        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,new CallTask(plugin,config.getString("hash")),10L,20L*60L*60);
        System.out.println("["+plugin.getDescription().getName()+"] Stats are being kept for this plugin. To opt-out for any reason, check plugins/stats.");
    }

    private static Boolean verifyConfig() {
        config.addDefault("opt-out", false);
        config.addDefault("hash", UUID.randomUUID().toString());

        if(!file.exists() || config.get("hash", null) == null) {
            System.out.println("BukkitStats is initializing for the first time. To opt-out check plugins/stats");
            try {
                config.options().copyDefaults(true);
                config.save(file);
            } catch (Exception ex) {
                System.out.println("BukkitStats failed to save.");
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }
}

class CallTask implements Runnable {
    private Plugin plugin;
    private String hash;

    public CallTask(Plugin plugin, String hash) {
        this.plugin = plugin;
        this.hash = hash;
    }

    public void run() {
        try {
            postUrl();
        } catch (Exception ignored) {
            System.out.println("Could not call home.");
            ignored.printStackTrace();
        }
    }

    private void postUrl() throws Exception {
        String url = String.format("http://usage.blockface.org/update.php?name=%s&build=%s&plugin=%s&port=%s&hash=%s&bukkit=%s",
                plugin.getServer().getName(),
                plugin.getDescription().getVersion().replaceAll(" ", "%20"),
                plugin.getDescription().getName().replaceAll(" ", "%20"),
                plugin.getServer().getPort(),
                hash,
                Bukkit.getVersion());
        new URL(url).openConnection().getInputStream();
    }
}