package com.moosemanstudios.GlowNightLight;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class GlowNightLight extends JavaPlugin {
	Logger log = Logger.getLogger("minecraft");
	public final GNLPlayerListener playerlistener = new GNLPlayerListener(this);
	public final GNLBlockListener blocklistener = new GNLBlockListener(this);
	public final HashMap<Player, ArrayList<Block>> nightLightUsers = new HashMap<Player, ArrayList<Block>>();
	static String mainDirectory = "plugins/NightLight";	// set main directory for easy reference
	public GNLTimeListener timeListener;
	public Configuration conf;	// conf.yml
	public Long nightStart, nightEnd;


	public void onDisable() {
		
		// save the files
		playerlistener.save();
		log.info("[GlowNightLight] is disabled!");
	}
	
	public void onEnable() {
		// get the config
		conf = this.getConfiguration();
		if (!propertyExists("nightstart")) {
			conf.setProperty("nightstart", 12000);
		}
		if (!propertyExists("nightend")) {
			conf.setProperty("nightend", 22200);
		}
		conf.save();
		
		// register the event
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerlistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blocklistener, Priority.Normal, this);
		
		// load the linked list
		new File(mainDirectory).mkdir();
		playerlistener.load();
		
		// setup the time listener
		timeListener = new GNLTimeListener(this, 0L, 0L);
		timeListener.run();
		
		// reload the config to place the correct times into the time listener
		reloadConfig();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled");
	}
	
	private boolean propertyExists(String path) {
		return this.getConfiguration().getProperty(path) != null;
	}
	
	private void reloadConfig() {
		conf.load();
		nightStart = (long) conf.getInt("nightstart", 12000);
		nightEnd = (long) conf.getInt("nightend", 22200);
		timeListener.setNightStart(nightStart);
		timeListener.setNightEnd(nightEnd);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		World world = player.getWorld();
		
		if (player instanceof Player) {
			// see if they want to enable/disable nightlight
			if (commandLabel.equalsIgnoreCase("gnl")) {
				if (player.hasPermission("glownightlight.nl")) {
					togglelight(player);
					return true;
				}
				else {
					player.sendMessage(ChatColor.RED + "You don't have permissions to do that");
					return false;
				}
			// see if they want to reload the config
			} else if (commandLabel.equalsIgnoreCase("gnlreload")) {
				if (player.hasPermission("glownightlight.reload")) {
					reloadConfig();
					// save and reload the hashmap
					playerlistener.save();
					playerlistener.load();
					player.sendMessage("Glow Night Light reloaded");
					log.info("[GlowNightLight] config reloaded");
				} else {
					player.sendMessage(ChatColor.RED + "You don't have the permissions to do that");
				}
			}
			// see if they want to change time to day
			if (commandLabel.equalsIgnoreCase("day")) {
				if (player.hasPermission("glownightlight.day")) {
					world.setTime(0);
					player.sendMessage("Time was moosed... its now 6:00AM");
					log.info("[GlowNightLight] " + player.getName() + " changed time to 6:00AM");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permissions to do that!");
					return false;
				}
			}
			
			// see if they want to change time to night
			if (commandLabel.equalsIgnoreCase("night")) {
				if (player.hasPermission("glownightlight.night")) {
					world.setTime(13800);
					player.sendMessage("Time was moosed... its now 7:48PM");
					log.info("[GlowNightLight] " + player.getName() + " changed time to 7:48PM");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permissions to do that!");
					return false;
				}
			}
			
			// see if they want to change time to dusk
			if (commandLabel.equalsIgnoreCase("dusk")) {
				if (player.hasPermission("glownightlight.dusk")) {
					world.setTime(12000);
					player.sendMessage("Time was moosed... its now 6:00PM");
					log.info("[GlowNightLight] " + player.getName() + " change time to 6:00PM");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permissions to do that!");
				}
			}
			
			// see if they want to change time to dawn
			if (commandLabel.equalsIgnoreCase("dawn")) {
				if (player.hasPermission("glownightlight.dawn")) {
					world.setTime(12000);
					player.sendMessage("Time was moosed... its now 4:12AM");
					log.info("[GlowNightLight] " + player.getName() + " change time to 4:12AM");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You don't have permissions to do that!");
				}
			}
		}
		return false;
	}
	
	private void togglelight(Player player) {
		if (enabled(player)) {
			this.nightLightUsers.remove(player);
			playerlistener.save();
			player.sendMessage(ChatColor.YELLOW + "Night Light disabled");
		}
		else {
			this.nightLightUsers.put(player, null);
			player.sendMessage(ChatColor.YELLOW + "Night Light enabled - right click to toggle block");
		}		
	}
	
	public boolean enabled(Player player) {
		return this.nightLightUsers.containsKey(player);
	}	
}
