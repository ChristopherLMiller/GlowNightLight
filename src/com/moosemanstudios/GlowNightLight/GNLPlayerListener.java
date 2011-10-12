package com.moosemanstudios.GlowNightLight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
	public HashMap<World, ArrayList<Block>> blockHash = new HashMap<World, ArrayList<Block>>();
	static String mainDirectory = "plugins/NightLight"; // set main directory
														// for easy reference

	GNLPlayerListener(GlowNightLight instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// see if they are in the hashmap
			if (plugin.enabled(player)) {
				// get the block that was clicked as well as what world
				Block block = event.getClickedBlock();
				World world = player.getWorld();

				// see if the block is glass or glowstone first
				Material blocktype = block.getType();
				if ((blocktype == Material.GLASS) || (blocktype == Material.GLOWSTONE)) {
					// see if the block is already in the hashmap
					if (enabled(world, block)) {
						// block is in list already, so remove it
						remove_block(world, block);
						player.sendMessage(ChatColor.YELLOW + world.getName() + " block removed");
					} else {
						// block needs added, so add it haha
						add_block(world, block);
						player.sendMessage(ChatColor.YELLOW + world.getName() + " block added");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "wrong block type");
				}
			}
		}
	}

	private Boolean enabled(World world, Block block) {
		// see if the hashmap has a key for the current world
		if (blockHash.containsKey(world)) {
			// hash map contains the world, extract the arraylist
			ArrayList<Block> blockArray = blockHash.get(world);

			// now see if the arraylist contains the block we are asking about
			if (blockArray.contains(block)) {
				// block is there, return true
				return true;
			} else {
				// block isn't in the list, return false
				return false;
			}
		}

		// defaults to false, if the world isn't in the hashmap there is
		// obviously no blocks enabled.
		return false;
	}

	public void add_block(World world, Block block) {
		// extract the ArrayList from the HashMap
		ArrayList<Block> blockArray = blockHash.get(world);

		// see if the blockarray is null
		if (blockArray == null) {
			blockArray = new ArrayList<Block>();
		}

		// add the block to the list
		blockArray.add(block);

		// save the array back to the hashmap
		blockHash.put(world, blockArray);
	}

	public void remove_block(World world, Block block) {
		// extract the array from the hash
		ArrayList<Block> blockArray = blockHash.get(world);

		// remove the block from the list
		blockArray.remove(block);

		// save the array back to the hashmap
		blockHash.put(world, blockArray);
	}

	public void save() {
		ArrayList<World> worlds = new ArrayList<World>(blockHash.keySet());

		for (World world : worlds) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(mainDirectory + "/" + world.getName() + ".txt"));

				// extract the arraylist from the hashmap
				ArrayList<Block> blockArray = blockHash.get(world);

				// iterate through the blockarray and write to the file
				for (Block block : blockArray) {
					out.write(block.getX() + ";" + block.getY() + ";" + block.getZ());
					out.newLine();
				}
				// log the success
				plugin.log.info("[GlowNightLight] world " + world.getName()	+ " saved.");
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void load() {
		// populate worlds with files read from the directory
		File dir = new File(mainDirectory);
		String[] files = dir.list();
		ArrayList<Block> blockArray = new ArrayList<Block>();
		blockHash.clear();

		// iterate throught files to get the filenames and add to list
		for (int i = 0; i < files.length; i++) {
			try {

				// see if the name of the file is config.yml, if so skip it ya noob LOL
				if (!files[i].equalsIgnoreCase("config.yml")) {

					// save the name of the world
					String worldName = getFileName(files[i]);
					World world = plugin.getServer().getWorld(worldName);

					if (world != null) {
						
						// open the file to read, file opened will be the name of the world loaded
						BufferedReader in = new BufferedReader(new FileReader(mainDirectory + "/" + files[i]));
	
						// populate the arraylist with values read from the file
						blockArray.clear();
	
						// loop through the file to get all the blocks
						String line = null;
						while ((line = in.readLine()) != null) {
							// split the string up
							String[] data = line.split(";");
	
							// make sure the block isn't malformed
							if (data.length == 3) {
								Block block = world.getBlockAt(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2]));
								blockArray.add(block);
							}
						}
	
						// at this point, everything has been read in, close the
						// file
						in.close();
	
						// add the arraylist as well as world to the hashmap
						blockHash.put(world, blockArray);
	
						// let player know world was loaded
						plugin.log.info("[GlowNightLight] world " + world.getName() + " loaded.");
					} else {
						plugin.log.info("[GlowNightLight] Failed to load world: " + worldName);
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private String getFileName(String fullFileName) {
		return fullFileName.substring(0, fullFileName.lastIndexOf('.'));
	}

}

