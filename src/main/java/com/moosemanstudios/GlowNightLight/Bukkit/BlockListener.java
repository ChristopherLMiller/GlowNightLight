package com.moosemanstudios.GlowNightLight.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
	private GlowNightLight plugin;
	
	public BlockListener(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// TODO: implement
	}

}
