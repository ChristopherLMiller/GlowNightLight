package com.moosemanstudios.GlowNightLight.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.h31ix.updater.Updater;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class GlowNightLight extends JavaPlugin {
	private String prefix = "[GlowNightLight] ";
	private Logger log = Logger.getLogger("minecraft");
	private PluginDescriptionFile pdfFile;
	public Boolean debug;
	public int nightStart, nightEnd;
	public Boolean updaterEnabled, updaterAuto, updaterNotify;
	
	@Override
	public void onEnable() {
		// load the config
		loadConfig();
		
		// metrics
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// check updater settings
		if (updaterEnabled) {
			if (updaterAuto) {
				Updater updater = new Updater(this, "glownightlight", this.getFile(), Updater.UpdateType.DEFAULT, true);
				if (updater.getResult() == Updater.UpdateResult.SUCCESS)
				getLog().info(getPrefix() + "update downloaded successfully, restart server to apply update");
			}
			if (updaterNotify) {
				getLog().info(getPrefix() + "Notifying admins as they login if update found");
				this.getServer().getPluginManager().registerEvents(new UpdaterPlayerListener(this), this);
			}
		}
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public Logger getLog() {
		return log;
	}
	
	public PluginDescriptionFile getDescriptionFile() {
		return pdfFile;
	}
	
	public void loadConfig() {
		// general settings
		if (!getConfig().contains("debug")) getConfig().set("debug", false);
		if (!getConfig().contains("night-start")) getConfig().set("night-start", 13000);
		if (!getConfig().contains("night-end")) getConfig().set("night-end", 23000);
		
		// updater settings
		if (!getConfig().contains("updater.enabled")) getConfig().set("updater.enabled", true);
		if (!getConfig().contains("udpater.auto")) getConfig().set("updater.auto", true);
		if (!getConfig().contains("updater.notify")) getConfig().set("updater.notify", false);
		
		saveConfig();
		
		debug = getConfig().getBoolean("debug");
		if (debug)
			getLog().info(getPrefix() + "debugging enabled");
		
		nightStart = getConfig().getInt("night-start");
		nightEnd = getConfig().getInt("night-end");
		if (debug) {
			getLog().info(getPrefix() + "Night start: " + nightStart);
			getLog().info(getPrefix() + "Night end: " + nightEnd);
		}
		
		updaterEnabled = getConfig().getBoolean("updater.enabled");
		updaterAuto = getConfig().getBoolean("updater.auto");
		updaterNotify = getConfig().getBoolean("updater.notify");
		if (debug) {
			if (updaterEnabled)
				getLog().info(getPrefix() + "Updater enabled");
			if (updaterAuto)
				getLog().info(getPrefix() + "Auto updating enabled");
			if (updaterNotify)
				getLog().info(getPrefix() + "notifying on update");
		}
	}
	
	public File getFileFolder() {
		return this.getFile();
	}
}
