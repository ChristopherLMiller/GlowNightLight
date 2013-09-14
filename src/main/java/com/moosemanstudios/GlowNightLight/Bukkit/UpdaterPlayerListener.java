package com.moosemanstudios.GlowNightLight.Bukkit;

import net.h31ix.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterPlayerListener implements Listener {
	
	private GlowNightLight plugin;
	
	UpdaterPlayerListener(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player=  event.getPlayer();
		
		if (player.hasPermission("glownightlight.admin")) {
			Updater updater = new Updater(plugin, "glownightlight", plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);
			
			if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE && plugin.updaterNotify) {
				player.sendMessage(ChatColor.AQUA + "An update is avaiable for GlowNightLight: " + updater.getLatestVersionString() + "(" + updater.getFileSize() + " bytes");
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.WHITE + "/gnl update" + ChatColor.AQUA + " to update");
			}
		}
	}

}
