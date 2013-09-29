package com.moosemanstudios.GlowNightLight.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.moosemanstudios.GlowNightLight.Core.BlockManager;
import com.moosemanstudios.GlowNightLight.Core.simpleBlock;

public class PlayerListener implements Listener {
	private GlowNightLight plugin;
	
	public PlayerListener(GlowNightLight plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		if (BlockManager.getInstance().playerEnabled(player.getName())) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (plugin.getConfig().getBoolean("worlds." + world.getName())) {
					Block block = event.getClickedBlock();
					Material blockType = block.getType();
					
					simpleBlock sblock = new simpleBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
					
					if ((blockType == Material.GLASS) || (blockType == Material.GLOWSTONE)) {
						if (BlockManager.getInstance().blockEnabled(sblock)) {
							if (BlockManager.getInstance().removeBlock(sblock)) {
								player.sendMessage(ChatColor.YELLOW + "block removed");
							}
						} else {
							if (BlockManager.getInstance().addBlock(sblock)) {
								player.sendMessage(ChatColor.YELLOW + "block added");
							}
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "Block must be glass or glowstone to add");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Glow Night Light disabled on this world");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// player quit the game, lets remove them from the list
		BlockManager.getInstance().removePlayer(event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		BlockManager.getInstance().removePlayer(event.getPlayer().getName());
	}
}
