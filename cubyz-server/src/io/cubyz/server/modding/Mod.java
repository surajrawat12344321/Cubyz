package io.cubyz.server.modding;

import io.cubyz.server.blocks.Block;
import io.cubyz.server.entity.EntityType;
import io.cubyz.server.items.Item;
import io.cubyz.utils.datastructures.Registry;
import io.cubyz.world.World;
import io.cubyz.world.biomes.Biome;

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
	public void registerBlocks(Registry<Block> registry) {
		
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
	
	/**
	 * Called when a new world is generated.
	 * Used for world-specific content.
	 * @param world
	 */
	public void onWorldGeneration(World world) {
		
	}
}
