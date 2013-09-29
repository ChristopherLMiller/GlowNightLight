package com.moosemanstudios.GlowNightLight.Core;

import java.util.HashSet;

public class BlockManager {
	private static BlockManager instance = null;
	
	private HashSet<String> players = new HashSet<String>();	// hashmap containing players with gnl enabled
	private HashSet<simpleBlock> blocks = new HashSet<simpleBlock>();
	private int nightStart, nightEnd;
	private Boolean activeDuringWeather;
	
	BlockManager() {} // exists to defeat default instantiation
	
	public static BlockManager getInstance() {
		if (instance == null) {
			instance = new BlockManager();
		}
		return instance;
	}
	
	public Boolean addPlayer(String player) {
		if (!players.contains(player)) {
			return players.add(player);
		} else {
			return true;
		}
	}
	
	public Boolean removePlayer(String player) {
		if (players.contains(player))
			return players.remove(player);
		else
			return true;
	}
	
	public int getNumPlayersEnabled() {
		return players.size();
	}
	
	public int getNumBlocks() {
		return blocks.size();
	}
	
	public int getNightStart() {
		return nightStart;
	}
	
	public int getNightEnd() {
		return nightEnd;
	}
	
	public Boolean getActiveDuringWeather() {
		return activeDuringWeather;
	}
	
	public Boolean setNightStart(String nightStart) {
		Integer.parseInt(nightStart);
		return true;
	}
	
	public Boolean setNightEnd(String nightEnd) {
		Integer.parseInt(nightEnd);
		return true;
	}
	
	public void init(int nightStart, int nightEnd, Boolean activeDuringWeather) {
		this.nightStart = nightStart;
		this.nightEnd = nightEnd;
		this.activeDuringWeather = activeDuringWeather;
	}

	public boolean playerEnabled(String name) {
		return players.contains(name);
	}

	public boolean blockEnabled(simpleBlock block) {
		return blocks.contains(block);
	}

	public boolean removeBlock(simpleBlock block) {
		return blocks.remove(block);
	}

	public boolean addBlock(simpleBlock block) {
		return blocks.add(block);
	}
}
