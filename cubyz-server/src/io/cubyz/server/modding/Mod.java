package io.cubyz.server.modding;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.world.biomes.Biome;
import io.cubyz.world.blocks.Block;
import io.cubyz.world.entity.EntityType;
import io.cubyz.world.items.Item;

public abstract class Mod {
	/**
	 * The first method called for all mods.
	 */
	public void init() {
		
	}
	
	/**
	 * Should only be used if data files are not flexible enough.
	 * @param registry
	 */
	public void registerItems(Registry<Item> registry) {
		
	}
	
	/**
	 * Should only be used if data files are not flexible enough.
	 * @param registry
	 */
	public void registerBlocks(Registry<Block> registry) {
		
	}
	
	/**
	 * Should only be used if data files are not flexible enough.
	 * @param registry
	 */
	public void registerEntities(Registry<EntityType> registry) {
		
	}
	
	/**
	 * Should only be used if data files are not flexible enough.
	 * @param registry
	 */
	public void registerBiomes(Registry<Biome> registry) {
		
	}
	
	/**
	 * Called after all mods have been inited. And all registry methods have been called.
	 */
	public void postInit() {
		
	}
	
	public abstract String getName();
}
