package com.moosemanstudios.GlowNightLight;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class GNLBlockListener extends BlockListener {
	public static GlowNightLight plugin;
	
	GNLBlockListener(GlowNightLight instance) {
		plugin = instance;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		// remove the block in the even from the blockhash if it exists
		if (plugin.blockHash.block_enabled(event.getBlock().getWorld(), event.getBlock())) {
			plugin.blockHash.remove_block(event.getBlock().getWorld(), event.getBlock());
		}
	}
}
