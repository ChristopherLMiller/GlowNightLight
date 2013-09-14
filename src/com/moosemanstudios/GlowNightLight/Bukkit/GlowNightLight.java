package com.moosemanstudios.GlowNightLight;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.blockface.stats.*;

public class GlowNightLight extends JavaPlugin {
	Logger log = Logger.getLogger("minecraft");
	public final GNLPlayerListener playerlistener = new GNLPlayerListener(this);	// player listener
	public final GNLBlockListener blocklistener = new GNLBlockListener(this);		// block listener
	public GNLTimeListener timeListener;											// timer for scheduler
	
	public final ArrayList<Player> nightLightUsers = new ArrayList<Player>();		// array of players who have GNL enabled
	
	public BlockHashMap blockHash;	// instance of the blockHashmap

	public Configuration config;	// conf.yml
	public Long nightStart, nightEnd;
	public Boolean activeWeather;


	public void onDisable() {
		// stop the timeListener thread
		timeListener.stop();
		
		// save the hashmap
		blockHash.save_blocks();

		log.info("[GlowNightLight] is disabled!");
	}
	
	public void onEnable() {
		// create the time listener
		timeListener = new GNLTimeListener(this, 0L, 0L);
		
		// create the blockhashmap class
		blockHash = new BlockHashMap(this);
		
		// get the config
		create_config();
		reload_config();
		
		// register the event
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerlistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blocklistener, Priority.Normal, this);
		
		// start the time listener thread
		timeListener.run();
		
		CallHome.load(this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled");
	}
	
	private void create_config() {
		if (!getConfig().contains("nightstart")) {
			getConfig().set("nightstart", 13000);
		}
		if (!getConfig().contains("nightend")) {
			getConfig().set("nightend", 23000);
		}
		if (!getConfig().contains("active-on-weather")) {
			getConfig().set("active-on-weather", true);
		}
		saveConfig();
	}
	
	private void reload_config() {
		// reload the block hash
		blockHash.reload_blocks();
		
		// get the variables
		nightStart = Long.valueOf(getConfig().getInt("nightstart"));
		nightEnd = Long.valueOf(getConfig().getInt("nightend"));
		activeWeather = getConfig().getBoolean("active-on-weather");
		
		// update the timelistener with the new values
		timeListener.set_values(nightStart, nightEnd, activeWeather);
	}
	
	private void save_config() {
		getConfig().set("nightstart", nightStart);
		getConfig().set("nightend", nightEnd);
		getConfig().set("active-on-weather", activeWeather);
		saveConfig();
	}
	
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
				if (sender.hasPermission("glownightlight.admin")) {
					sender.sendMessage(ChatColor.RED + "/gnl reload" + ChatColor.WHITE + ": Reloads the configuration file");
					sender.sendMessage(ChatColor.RED + "/gnl start [time]" + ChatColor.WHITE + ": Sets time when glass turns to glowstone");
					sender.sendMessage(ChatColor.RED + "/gnl stop [time]" + ChatColor.WHITE + ": Sets time when glowstone converts back to glass");
					sender.sendMessage(ChatColor.RED + "/gnl weather (enable/disable)" + ChatColor.WHITE + ": Enables or disables weather having affect on glowstone");
				}
				if (sender.hasPermission("glownightlight.nl")) {
					sender.sendMessage(ChatColor.RED + "/gnl (enable/disable)" + ChatColor.WHITE + ": Enables/disables ability to toggle blocks");
				}
				if (sender.hasPermission("glownightlight.time")) {
					sender.sendMessage(ChatColor.RED + "/dawn" + ChatColor.WHITE + ": Sets time on current world to dawn");
					sender.sendMessage(ChatColor.RED + "/day" + ChatColor.WHITE + ": Sets time on current world to day");
					sender.sendMessage(ChatColor.RED + "/dusk" + ChatColor.WHITE + ": Sets time on current world to dusk");
					sender.sendMessage(ChatColor.RED + "/night" + ChatColor.WHITE + ": Sets time on current world to night");
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
			
			if (split[0].equalsIgnoreCase("weather")) {
				if (sender.hasPermission("glownightlight.admin")) {
					if (split[1].equalsIgnoreCase("enable")) {
						activeWeather = true;
						timeListener.set_active_weather(activeWeather);
						save_config();
						sender.sendMessage("Glowstone active on weather enabled");
					} else if (split[1].equalsIgnoreCase("disable")) {
						activeWeather = false;
						timeListener.set_active_weather(activeWeather);
						save_config();
						sender.sendMessage("Glowstone active on weather disabled");
					} else {
						sender.sendMessage(ChatColor.RED + "Error, invalid command, please use " + ChatColor.WHITE + "/gnl help" + ChatColor.RED + "  for help");
					}
				}	
				return true;
			}
			
			if (split[0].equalsIgnoreCase("start")) {
				if (sender.hasPermission("glownightlight.admin")) {
					Long newTime = Long.valueOf(split[1]);
					
					if (newTime != null) {
						nightStart = newTime;
						timeListener.set_night_start(nightStart);
						save_config();
						sender.sendMessage("Night start updated successfully");
					} else {
						sender.sendMessage(ChatColor.RED + "Error, invalid number, please use " + ChatColor.WHITE + "/gnl help" + ChatColor.RED + " for help");
					}
				}
				return true;
			}
			
			if (split[0].equalsIgnoreCase("end")) {
				if (sender.hasPermission("glownightlight.admin")) {
					Long newTime = Long.valueOf(split[1]);
					
					if (newTime != null) {
						nightEnd = newTime;
						timeListener.set_night_end(nightEnd);
						save_config();
						sender.sendMessage("Night end updated successfully");
					} else {
						sender.sendMessage(ChatColor.RED + "Error, invalid number, please use " + ChatColor.WHITE + "/gnl help" + ChatColor.RED + " for help");
					}
				}
				return true;
			}
			
			if (split[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("glownightlight.admin")) {
					reload_config();
					timeListener.set_values(nightStart, nightEnd, activeWeather);
					
					// reload the hashmap
					blockHash.reload_blocks();

					sender.sendMessage("Glow Night Light reloaded");
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permissions to do that");
				}
				return true;
			}
			return false;
		}
		
		if (commandName.equalsIgnoreCase("dawn")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("glownightlight.time")) {
					setTime(((Player) sender).getWorld(), 23000L);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Only players can issue this command");
			}
			return true;
		}
		
		if (commandName.equalsIgnoreCase("day")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("glownightlight.time")) {
					setTime(((Player) sender).getWorld(), 6000L);
				}
			}
			return true;
		}
		
		if (commandName.equalsIgnoreCase("dusk")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("glownightlight.time")) {
					setTime(((Player) sender).getWorld(), 13000L);
				}
			}
			return true;
		}
		
		if (commandName.equalsIgnoreCase("night")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("glownightlight.time")) {
					setTime(((Player) sender).getWorld(), 18000L);
				}
			}
			return true;
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
			nightLightUsers.remove(player);

			blockHash.save_blocks();
			
			
			player.sendMessage(ChatColor.YELLOW + "Night Light disabled");
		}
		else {
			this.nightLightUsers.add(player);
			player.sendMessage(ChatColor.YELLOW + "Night Light enabled - right click to toggle block");
		}		
	}
	
	public boolean enabled(Player player) {
		return this.nightLightUsers.contains(player);
	}	
}
