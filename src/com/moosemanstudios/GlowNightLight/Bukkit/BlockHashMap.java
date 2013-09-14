package com.moosemanstudios.GlowNightLight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockHashMap {
	public HashMap<World, ArrayList<Block>> blockHash = new HashMap<World, ArrayList<Block>>();
	static String mainDirectory = "plugins/GlowNightLight";
	private final GlowNightLight plugin;
	
	public BlockHashMap(GlowNightLight instance) {
		plugin = instance;
	}
	public void add_block(World world, Block block) {
		// extract the world from the hashmap
		ArrayList<Block> blockArray = blockHash.get(world);
		
		// see if the blockArray is null, if so create it
		if (blockArray == null) {
			blockArray = new ArrayList<Block>();
		}
		
		// add the block to the list
		blockArray.add(block);
		
		// save the blockArray back to the hashmap
		blockHash.put(world, blockArray);
	}
	
	public void remove_block(World world, Block block) {
		// extract the world fro mthe hashmap
		ArrayList<Block> blockArray = blockHash.get(world);
		
		// see if the block array is null, if so exit
		if (blockArray == null) {
			return;
		} else {
			blockArray.remove(block);
		}
		
		// save back to the hashmap
		blockHash.put(world, blockArray);
	}
	
	public void save_blocks() {
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
	
	public void reload_blocks() {
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
	
	public ArrayList<Block> getBlocks(World world) {
		return blockHash.get(world);
	}
	
	public Boolean contains_world(World world) {
		return blockHash.containsKey(world);
	}
	
	public Boolean block_enabled(World world, Block block) {
		// Description: see if the specified block is enabled in the specified world
		// Inputs: World - the world to test on
		//         Block - the block to be checked
		// Outputs: Boolean - whether the block exists or not
		
		// extract the ArrayList for the specified world out
		ArrayList<Block> blockList = blockHash.get(world);
		
		if (blockList != null) {
			// see if the block exists
			if (blockList.contains(block)) {
				return true;
			} else {
				return false;
			}
		} else {
			// the world doesn't exist, go ahead and create it
			blockHash.put(world, null);
			plugin.log.info("[GlowNightLight] Error checking block, world doesn't exist, adding now");
			return false;
		}
	}
	
	private String getFileName(String fullFileName) {
		return fullFileName.substring(0, fullFileName.lastIndexOf('.'));
	}
}
