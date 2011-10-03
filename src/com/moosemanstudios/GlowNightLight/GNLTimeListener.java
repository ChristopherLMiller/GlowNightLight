package com.moosemanstudios.GlowNightLight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.moosemanstudios.GlowNightLight.GlowNightLight;

public class GNLTimeListener {
	private static Long NIGHT_END;
	private static Long NIGHT_START;
	private static GlowNightLight plugin;

	public GNLTimeListener(GlowNightLight instance, Long nightStart, Long nightEnd) {
		plugin = instance;
		
		NIGHT_END = nightEnd;
		NIGHT_START = nightStart;
		
	}
	
	public void run() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			public void run() {
				// get list of worlds
				List<World> worlds = plugin.getServer().getWorlds();
				
				for (World world : worlds) {
					// see if the world is in the hashmap
					if (plugin.playerlistener.blockHash.containsKey(world)) {
						// get the time in the world
						long time = world.getTime();
						
						// if the time is between the intervals, then check the blocks and update them
						if ((time > NIGHT_START) && (time < NIGHT_END)) {
							ArrayList<Block> blockArray = plugin.playerlistener.blockHash.get(world);
							
							for (Block block: blockArray) {
								if (block.getType() != Material.GLOWSTONE) {
									block.setType(Material.GLOWSTONE);
								}
							}
						} else {
							ArrayList<Block> blockArray = plugin.playerlistener.blockHash.get(world);
							
							for(Block block : blockArray) {
								if (block.getType() != Material.GLASS) {
									block.setType(Material.GLASS);
								}
							}
						}
					}
				}
			}
		}, 100L, 100L);
	}
	
	public Long getNightStart() {
		return NIGHT_START;
	}
	
	public Long getNightEnd() {
		return NIGHT_END;
	}
	
	public void setNightStart(Long start) {
		NIGHT_START = start;
	}
	
	public void setNightEnd(Long end) {
		NIGHT_END = end;
	}
}

