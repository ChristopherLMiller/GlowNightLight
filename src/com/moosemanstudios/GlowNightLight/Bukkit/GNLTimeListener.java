package com.moosemanstudios.GlowNightLight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.moosemanstudios.GlowNightLight.GlowNightLight;

@SuppressWarnings("unused")
public class GNLTimeListener {
	private static Long NIGHT_END;
	private static Long NIGHT_START;
	private static Boolean ACTIVE_DURING_WEATHER;
	private static GlowNightLight plugin;

	public GNLTimeListener(GlowNightLight instance, Long nightStart, Long nightEnd) {
		plugin = instance;
		NIGHT_END = nightEnd;
		NIGHT_START = nightStart;
		
	}
	
	public void set_values(Long nightStart, Long nightEnd, Boolean activeWeather) {
		NIGHT_START = nightStart;
		NIGHT_END = nightEnd;
		ACTIVE_DURING_WEATHER = activeWeather;
	}
	
	public void run() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			public void run() {
				// get list of worlds
				List<World> worlds = plugin.getServer().getWorlds();
				
				// loop through the worlds
				for (World world : worlds) {
					// see if the world is in the hashmap
					if (plugin.blockHash.contains_world(world)) {
						// get the time in the world
						long time = world.getTime();
						
						ArrayList<Block> blockArray = plugin.blockHash.getBlocks(world);
						if ( (time >= NIGHT_START) && (time <= NIGHT_END) ) {
							for (Block block : blockArray) {
								if (block.getType() != Material.GLOWSTONE){
									block.setType(Material.GLOWSTONE);
								}
							}
						} else if (ACTIVE_DURING_WEATHER) {
							// its not night, see if there is any weather events currently
							if (world.hasStorm()) {
								int durationLeft = world.getWeatherDuration();
								
								for (Block block : blockArray) {
									if (block.getType() != Material.GLOWSTONE) {
										block.setType(Material.GLOWSTONE);
									}
								}
							} else {
								for (Block block : blockArray) {
									if (block.getType() != Material.GLASS) {
										block.setType(Material.GLASS);
									}
								}
							}
						} else {
							for (Block block : blockArray) {
								if (block.getType() != Material.GLASS) {
									block.setType(Material.GLASS);
								}
							}
						}
					} else {
						// no sense in doing anything, save the cycles!
					}
				}
			}
		}, 100L, 100L);
	}
	
	public void stop() {
		plugin.getServer().getScheduler().cancelTasks(plugin);
	}
	
	public void set_night_start(Long nightStart) {
		NIGHT_START = nightStart;
	}
	
	public Long get_night_start() {
		return NIGHT_START;
	}
	
	public void set_night_end(Long nightEnd) {
		NIGHT_END = nightEnd;
	}
	
	public Long get_night_end() {
		return NIGHT_END;
	}
	
	public void set_active_weather(Boolean activeWeather) {
		ACTIVE_DURING_WEATHER = activeWeather;
	}
	
	public Boolean get_active_weather() {
		return ACTIVE_DURING_WEATHER;
	}
}

