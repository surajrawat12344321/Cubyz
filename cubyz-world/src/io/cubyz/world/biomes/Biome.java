package io.cubyz.world.biomes;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * TODO
 */
public class Biome implements RegistryElement {
	private final String ID;
	
	public Biome(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String getID() {
		return ID;
	}
}
