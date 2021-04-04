package io.cubyz.world.entity;

import java.util.ArrayList;

/**
 * Stores all entities that are inside one chunk.<br>
 * This allows to easily find and save all entities when a chunk gets unloaded.
 */
public class ChunkEntityManager {
	private ArrayList<Entity> entities = new ArrayList<>();
	
	public void update() {
		for(int i = 0; i < entities.size(); i++) {
			// Update the entities:
			entities.get(i).update();
			// Check if the entity is still inside this chunk:
			// TODO
		}
		// Take the last effect and test if it is over.
	}
}
