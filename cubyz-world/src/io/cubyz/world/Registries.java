package io.cubyz.world;

import io.cubyz.utils.datastructures.DataOrientedRegistry;
import io.cubyz.utils.datastructures.Registry;
import io.cubyz.world.biomes.Biome;
import io.cubyz.world.blocks.Blocks;
import io.cubyz.world.blocks.RotationMode;
import io.cubyz.world.entity.Effect;
import io.cubyz.world.entity.EntityComponent;
import io.cubyz.world.entity.EntityType;
import io.cubyz.world.items.Item;

/**
 * All the registries of the current universe.
 */

public class Registries {
	// Registries that may change per world:
	public static final Registry<Item> ITEMS = new Registry<Item>();
	public static final Registry<EntityType> ENTITIES = new Registry<EntityType>();
	public static final Registry<Biome> BIOMES = new Registry<Biome>();
	public static final Registry<Effect> EFFECTS = new Registry<Effect>();
	public static final Registry<EntityComponent> ENTITY_COMPONENTS = new Registry<EntityComponent>();
	
	// Registries of Registries:
	public static final Registry<DataOrientedRegistry> BLOCK_REGISTRIES = new Registry<DataOrientedRegistry>();
	
	// Registries that stay constant:
	public static final Registry<RotationMode> ROTATION_MODES = new Registry<RotationMode>();
	
	
	
	public static void clear() {
		ITEMS.clear();
		ROTATION_MODES.clear();
		ENTITIES.clear();
		ENTITY_COMPONENTS.clear();
		BIOMES.clear();
		EFFECTS.clear();
		
		for(DataOrientedRegistry registry : BLOCK_REGISTRIES.toArray(new DataOrientedRegistry[0])) {
			registry.clear();
		}
	}
}
