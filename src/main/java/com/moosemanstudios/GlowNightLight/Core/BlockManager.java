package com.moosemanstudios.GlowNightLight.Core;

import java.util.HashSet;

public class BlockManager {
	private static BlockManager instance = null;
	
	private HashSet<String> players;	// hashmap containing players with gnl enabled
	private HashSet<Block> blocks;	// hashset of blocks to change
	
	BlockManager() {} // exists to defeat default instantiation
	
	public static BlockManager getInstance() {
		if (instance == null) {
			instance = new BlockManager();
		}
		return instance;
	}
	
	public Boolean addPlayer(String player) {
		return players.add(player);
	}
	
	public Boolean removePlayer(String player) {
		return players.remove(player);
	}
	
}
