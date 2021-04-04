package io.cubyz.world;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.world.biomes.Biome;
import io.cubyz.world.blocks.Block;
import io.cubyz.world.entity.Effect;
import io.cubyz.world.entity.EntityComponent;
import io.cubyz.world.entity.EntityType;
import io.cubyz.world.items.Item;

/**
 * All the registries of the current universe.
 */

public class Registries {
	public static final Registry<Item> ITEMS = new Registry<Item>();
	public static final Registry<Block> BLOCKS = new Registry<Block>();
	public static final Registry<EntityType> ENTITIES = new Registry<EntityType>();
	public static final Registry<EntityComponent> ENTITY_COMPONENTS = new Registry<EntityComponent>();
	public static final Registry<Biome> BIOMES = new Registry<Biome>();
	public static final Registry<Effect> EFFECTS = new Registry<Effect>();
	
	
	
	public static void clear() {
		ITEMS.clear();
		BLOCKS.clear();
		ENTITIES.clear();
		ENTITY_COMPONENTS.clear();
		BIOMES.clear();
		EFFECTS.clear();
	}
}
