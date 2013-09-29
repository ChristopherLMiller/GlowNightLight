package com.moosemanstudios.GlowNightLight.Core;

public class simpleBlock {
	private int x, y, z;
	private String world;
	
	public simpleBlock(int x, int y, int z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public String getWorld() {
		return world;
	}
	
	public Boolean compare(simpleBlock block) {
		return (this.x == block.getX()) && (this.y == block.getY()) && (this.z == block.getZ()) && (this.world == block.getWorld());
	}
}
