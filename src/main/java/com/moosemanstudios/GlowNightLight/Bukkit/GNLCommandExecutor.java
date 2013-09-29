package com.moosemanstudios.GlowNightLight.Bukkit;

import net.h31ix.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moosemanstudios.GlowNightLight.Core.BlockManager;

public class GNLCommandExecutor implements CommandExecutor {
	private GlowNightLight plugin;
	private CommandSender sender;
	
	public GNLCommandExecutor(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String commandName = cmd.getName().toLowerCase();
		
		this.sender = sender;
		
		if (commandName.equalsIgnoreCase("gnl")) {
			if (args.length == 0) {
				showHelp();
			} else {
				if (args[0].equalsIgnoreCase("help")) {
					showHelp();
				} else if (args[0].equalsIgnoreCase("version")) {
					showVersion();
				} else if (args[0].equalsIgnoreCase("stats")) {
					stats();
				} else if (args[0].equalsIgnoreCase("enable")) {
					enable(true);
				} else if (args[0].equalsIgnoreCase("disable")) {
					enable(false);
				} else if (args[0].equalsIgnoreCase("reload")) {
					reload();
				} else if (args[0].equalsIgnoreCase("start")) {
					startTime(args);
				} else if (args[0].equalsIgnoreCase("stop")) {
					stopTime(args);
				} else if (args[0].equalsIgnoreCase("weather")) {
					setWeather(args);
				} else if (args[0].equalsIgnoreCase("update")) {
					update();
				} else {
					sender.sendMessage(ChatColor.RED + "Unknown command, Type " + ChatColor.WHITE + "/gnl help" + ChatColor.RED + " for help"); 
				}
			}
			
			return true;
		}
		return false;
	}
	
	public void showVersion() {
		sender.sendMessage(ChatColor.GOLD + "GlowNightLight Version: " + ChatColor.WHITE + plugin.getDescription().getVersion() + ChatColor.GOLD + " - Author: moose517");
	}
	
	public void enable(Boolean enable) {
		if (sender instanceof Player) {
			if (sender.hasPermission("glownightlight.nl")) {
				if (enable) {
					if (BlockManager.getInstance().addPlayer(sender.getName())) {
						sender.sendMessage(ChatColor.YELLOW + "Night light enabled - right click to toggle blocks");
					} else {
						sender.sendMessage(ChatColor.RED + "Unable to enable");
					}
				} else {
					if (BlockManager.getInstance().removePlayer(sender.getName())) {
						sender.sendMessage(ChatColor.YELLOW + "Night light disabled");
					} else {
						sender.sendMessage(ChatColor.RED + "Unable to disable");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Missing required permission: " + ChatColor.WHITE + "glownightlight.nl");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Must be a player");
		}
	}
	
	public void stats() {
		sender.sendMessage(ChatColor.YELLOW + "GlowNightLight - Stats");
		sender.sendMessage(ChatColor.YELLOW + "------------------------------");
		sender.sendMessage(ChatColor.GOLD + "Night start: " + ChatColor.WHITE + Integer.toString(BlockManager.getInstance().getNightStart()));
		sender.sendMessage(ChatColor.GOLD + "Night end: " + ChatColor.WHITE + Integer.toString(BlockManager.getInstance().getNightEnd()));
		sender.sendMessage(ChatColor.GOLD + "Active during weather: " + ChatColor.WHITE + String.valueOf(BlockManager.getInstance().getActiveDuringWeather()));
		sender.sendMessage(ChatColor.GOLD + "No. Players enabled: " + ChatColor.WHITE + BlockManager.getInstance().getNumPlayersEnabled());
		sender.sendMessage(ChatColor.GOLD + "No. Blocks: " + ChatColor.WHITE + BlockManager.getInstance().getNumBlocks());
	}
	
	public void reload() {
		// TODO: need to finish up block manager to know how to handle here
	}
	
	public void startTime(String[] args) {
		if (sender.hasPermission("glownightlight.admin")) {
			if (args.length >= 2) {
				BlockManager.getInstance().setNightStart(args[1]);
				plugin.setConfig("night-start", BlockManager.getInstance().getNightStart());
			} else {
				sender.sendMessage(ChatColor.RED +"Must provide only a start time");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission: " + ChatColor.WHITE + "glownightlight.admin");
		}
	}
	
	public void stopTime(String[] args) {
		if (sender.hasPermission("glownightlight.admin")) {
			if (args.length >= 2) {
				BlockManager.getInstance().setNightStart(args[1]);
				plugin.setConfig("night-end", BlockManager.getInstance().getNightEnd());
			} else {
				sender.sendMessage(ChatColor.RED +"Must provide only a end time");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission: " + ChatColor.WHITE + "glownightlight.admin");
		}
	}
	
	public void setWeather(String[] args) {
		
	}
	
	public void update() {
		if (sender.hasPermission("glownightlight.admin")) {
			if (plugin.updaterEnabled) { 
				Updater updater = new Updater(plugin, "GlowNightLight", plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
				if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
					sender.sendMessage(ChatColor.AQUA + "Update found, starting download: " + updater.getLatestVersionString() + "(" + updater.getFileSize() + "bytes)");
					updater = new Updater(plugin, "GlowNightLight", plugin.getFileFolder(), Updater.UpdateType.DEFAULT, true);
					
					switch (updater.getResult()) {
					case FAIL_BADSLUG:
						sender.sendMessage(ChatColor.AQUA + "Slug was bad, report this to moose517 on dev.bukkit.org");
						break;
					case FAIL_DBO:
						sender.sendMessage(ChatColor.AQUA + "Dev.bukkit.org couldn't be contacted, try again later");
						break;
					case FAIL_DOWNLOAD:
						sender.sendMessage(ChatColor.AQUA + "File download failed");
						break;
					case FAIL_NOVERSION:
						sender.sendMessage(ChatColor.AQUA + "Unable to check version on dev.bukkit.org, notify moose517");
						break;
					case NO_UPDATE:
						break;
					case SUCCESS:
						sender.sendMessage(ChatColor.AQUA + "Update downloaded successfully, restart server to apply update");
						break;
					case UPDATE_AVAILABLE:
						sender.sendMessage(ChatColor.AQUA + "Update found but not downloaded");
						break;
					default:
						sender.sendMessage(ChatColor.RED + "Shoudn't have had this happen, contact moose517");
						break;
					}
				} else {
					sender.sendMessage(ChatColor.AQUA + "No updates found");
				}
			} else {
				sender.sendMessage(ChatColor.AQUA + "Updater not enabled.  Enabled in config");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Missing required permission: " + ChatColor.WHITE + "glownightlight.admin");
		}
	}
	
	public void showHelp() {
		sender.sendMessage("/gnl help" + ChatColor.RED + ": Display this help screen");
		sender.sendMessage("/gnl version " + ChatColor.RED + ": Show plugin verion");
		sender.sendMessage("/gnl stats" + ChatColor.RED + ": Stats about plugin");
		
		if (sender.hasPermission("glownightlight.nl")) {
			sender.sendMessage("/gnl <enable/disable>" + ChatColor.RED + ": Enable/disable ability to toggle blocks");
		}
		if (sender.hasPermission("glownightlight.admin")) {
			sender.sendMessage("/gnl reload" + ChatColor.RED + ": Reloads the files");
			sender.sendMessage("/gnl start [time]" + ChatColor.RED + ": Sets start time");
			sender.sendMessage("/gnl stop [time]" + ChatColor.RED + ": Sets stop time");
			sender.sendMessage("/gnl weather <enable/disable>" + ChatColor.RED + ": Enable/disable weather having effect on changing blocks");
			if (plugin.updaterEnabled)
				sender.sendMessage("/gnl update" + ChatColor.RED + ": Check for plugin update");
		}
	}
}
