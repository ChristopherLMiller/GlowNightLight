package com.moosemanstudios.GlowNightLight.Bukkit;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.moosemanstudios.GlowNightLight.Core.BlockManager;
import com.moosemanstudios.GlowNightLight.Core.simpleBlock;

public class BlockListener implements Listener {
	private GlowNightLight plugin;
	
	public BlockListener(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		simpleBlock sblock = new simpleBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
		
		BlockManager.getInstance().removeBlock(sblock);
	}

}
