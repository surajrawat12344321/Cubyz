package io.meshes;

import io.cubyz.utils.datastructures.DataOrientedRegistry;
import io.cubyz.utils.json.JsonObject;

/**
 * Stores all the mesh data for the blocks. Uses the same ID-ing as `Blocks`.
 * TODO.
 */

public class BlockMeshes implements DataOrientedRegistry {

	@Override
	public String getID() {
		return "cubyz:block_meshes";
	}

	@Override
	public void clear() {
		// TODO
		
	}

	@Override
	public void register(String registryID, JsonObject json) {
		// TODO
		System.out.println(registryID);
	}
	
}
