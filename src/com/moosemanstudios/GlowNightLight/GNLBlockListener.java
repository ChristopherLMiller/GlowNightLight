package com.moosemanstudios.GlowNightLight;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

@SuppressWarnings("unused")
public class GNLBlockListener extends BlockListener {
	public static GlowNightLight plugin;
	
	GNLBlockListener(GlowNightLight instance) {
		plugin = instance;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		plugin.playerlistener.remove_block(event.getBlock().getWorld(), event.getBlock());
	}
}
