package com.moosemanstudios.GlowNightLight;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class GNLPlayerListener extends PlayerListener {
	public static GlowNightLight plugin;

	GNLPlayerListener(GlowNightLight instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		// get the player fro mthe event
		Player player = event.getPlayer();
		
		// see if they right clicked
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// see if they are in the hashmap
			if (plugin.enabled(player)) {
				// get the block the player clicked on, including the world
				Block block = event.getClickedBlock();
				World world = player.getWorld();
				
				// see if the block is glass or glowstone first
				Material blockType = block.getType();
				if ((blockType == Material.GLASS) || (blockType == Material.GLOWSTONE)) {
					// see if the block is in the hashmap
					if (plugin.blockHash.block_enabled(world, block)) {
						// block is enabled already, remove it
						plugin.blockHash.remove_block(world, block);
						player.sendMessage(ChatColor.YELLOW + world.getName() + " block removed");
					} else {
						// block needs added, do it!
						plugin.blockHash.add_block(world, block);
						player.sendMessage(ChatColor.YELLOW + world.getName() + " block added");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "wrong block type!");
				}
			}
		}
	}
}

