package com.moosemanstudios.GlowNightLight;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GlowNightLight extends JavaPlugin {
	Logger log = Logger.getLogger("minecraft");
	public final GNLPlayerListener playerlistener = new GNLPlayerListener(this);
	public final GNLBlockListener blocklistener = new GNLBlockListener(this);
	public final HashMap<Player, ArrayList<Block>> nightLightUsers = new HashMap<Player, ArrayList<Block>>();
	static String mainDirectory = "plugins/NightLight";	// set main directory for easy reference
	public GNLTimeListener timeListener;
	public Configuration config;	// conf.yml
	public Long nightStart, nightEnd;


	public void onDisable() {
		
		// save the files
		playerlistener.save();
		log.info("[GlowNightLight] is disabled!");
	}
	
	public void onEnable() {
		// get the config
		config = this.getConfig();
		if (!config.contains("nightstart")) {
			config.set("nightstart", 12000);
		}
		if (!config.contains("nightend")) {
			config.set("nightend", 22200);
		}
		saveConfig();
		
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
	
	private void reloadConfig() {		
		nightStart = config.getLong("nightstart");
		nightEnd = config.getLong("nightend");
		timeListener.setNightStart(nightStart);
		timeListener.setNightEnd(nightEnd);
	}
	
	// TODO: REVISE ME!!!!!!!!	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String[] split = args;
		String commandName = cmd.getName().toLowerCase();
		
		if (commandName.equalsIgnoreCase("gnl")) {
			if (split.length == 0) {
				sender.sendMessage(ChatColor.RED + "Type " + ChatColor.WHITE + "/gnl help" + ChatColor.RED + " for help");
				return true;
			}
			
			if (split[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.RED + "Glow Night Light Help");
				sender.sendMessage("--------------------------------------");
				sender.sendMessage(ChatColor.RED + "/gnl help" + ChatColor.WHITE + ": Displays help screen");
				
				// rest of the help menu is based on the users permissions
				if (sender.hasPermission("glownightlight.reload")) {
					sender.sendMessage(ChatColor.RED + "/gnl reload" + ChatColor.WHITE + ": Reloads the configuration file");
				}
				if (sender.hasPermission("glownightlight.nl")) {
					sender.sendMessage(ChatColor.RED + "/gnl (enable/disable)" + ChatColor.WHITE + ": Enables/disables ability to toggle blocks");
				}
				if (sender.hasPermission("glownightlight.time")) {
					sender.sendMessage(ChatColor.RED + "/gnl dawn" + ChatColor.WHITE + ": Sets time on current world to dawn");
					sender.sendMessage(ChatColor.RED + "/gnl midday" + ChatColor.WHITE + ": Sets time on current world to midday");
					sender.sendMessage(ChatColor.RED + "/gnl dusk" + ChatColor.WHITE + ": Sets time on current world to dusk");
					sender.sendMessage(ChatColor.RED + "/gnl midnight" + ChatColor.WHITE + ": Sets time on current world to midnight");
				}
				return true;
			}
			
			if (split[0].equalsIgnoreCase("enable") || split[0].equalsIgnoreCase("disable")) {
				// make sure its a player
				if (sender instanceof Player) {
					Player player = (Player) sender;
					
					if (player.hasPermission("glownightlight.nl")) {
						if (split[0].equalsIgnoreCase("enable")) {
							if (!enabled(player)) {
								togglelight(player);
							}
						} else {
							if (enabled(player)) {
								togglelight(player);
							}
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can issue this command");
				}
				return true;
			}
			
			if (split[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("glownightlight.reload")) {
					reloadConfig();
					
					// save and reload the hashmap
					playerlistener.save();
					playerlistener.load();
					sender.sendMessage("Glow Night Light reloaded");
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permissions to do that");
				}
				return true;
			}
			
			if (split[0].equalsIgnoreCase("dawn") || split[0].equalsIgnoreCase("midday") || split[0].equalsIgnoreCase("dusk") || split[0].equalsIgnoreCase("midnight")) {
				if (sender instanceof Player) {
					String command = split[0];
					Player player = (Player) sender;
					if (player.hasPermission("glownightlight.time")) {
						if (command.equalsIgnoreCase("dawn")) {
							setTime(player.getWorld(), 0L);							
						} else if (command.equalsIgnoreCase("midday")) {
							setTime(player.getWorld(), 6000L);
						} else if (command.equalsIgnoreCase("dusk")) {
							setTime(player.getWorld(), 12000L);
						} else {
							setTime(player.getWorld(), 18000L);
						}
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permissiosn to do that");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can issue this command");
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	private void setTime(World world, Long time) {
		world.setTime(time);
		
		// get list of all players on the world
		List<Player> players = world.getPlayers();
		
		for (Player player : players) {
			player.sendMessage("Time was moosed.... it's now " + time);
		}
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
