package com.moosemanstudios.GlowNightLight.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {
	private GlowNightLight plugin;
	
	public PlayerListener(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// TODO
	}

}
