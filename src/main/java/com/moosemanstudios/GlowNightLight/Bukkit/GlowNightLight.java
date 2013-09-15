package com.moosemanstudios.GlowNightLight.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.h31ix.updater.Updater;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.moosemanstudios.GlowNightLight.Core.BlockManager;

public class GlowNightLight extends JavaPlugin {
	private String prefix = "[GlowNightLight] ";
	private Logger log = Logger.getLogger("minecraft");
	private PluginDescriptionFile pdfFile;
	public Boolean debug;
	public int nightStart, nightEnd;
	public Boolean updaterEnabled, updaterAuto, updaterNotify;
	public Boolean activeDuringWeather;
	
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
				Updater updater = new Updater(this, "GlowNightLight", this.getFile(), Updater.UpdateType.DEFAULT, true);
				if (updater.getResult() == Updater.UpdateResult.SUCCESS)
				getLog().info(getPrefix() + "update downloaded successfully, restart server to apply update");
			}
			if (updaterNotify) {
				getLog().info(getPrefix() + "Notifying admins as they login if update found");
				this.getServer().getPluginManager().registerEvents(new UpdaterPlayerListener(this), this);
			}
		}
		
		// register the listeners
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		
		// register the command executor
		getCommand("gnl").setExecutor(new GNLCommandExecutor(this));
		
		// initialize the block manager
		BlockManager.getInstance().init(nightStart, nightEnd, activeDuringWeather);
		
		getLog().info(getPrefix() + "is now enabled");
	}
	
	@Override
	public void onDisable() {
		getLog().info(getPrefix() + " is now disabled");
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
		if (!getConfig().contains("active-during-weather")) getConfig().set("active-during-weather", true);
		
		// updater settings
		if (!getConfig().contains("updater.enabled")) getConfig().set("updater.enabled", false);
		if (!getConfig().contains("updater.auto")) getConfig().set("updater.auto", false);
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
		
		activeDuringWeather = getConfig().getBoolean("active-during-weather");
		if (debug)
			if (activeDuringWeather)
					getLog().info(getPrefix() + "Active during weather: true");
		
		// updater
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
		
		getLog().info(getPrefix() + "Config loaded.");
	}
	
	public void setConfig(String key, Object value) {
		getConfig().set(key, value);
		saveConfig();
	}
	
	public File getFileFolder() {
		return this.getFile();
	}
}
