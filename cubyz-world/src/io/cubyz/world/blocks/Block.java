package io.cubyz.world.blocks;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * TODO
 */
public class Block implements RegistryElement {
	private final String ID;
	
	public Block(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String getID() {
		return ID;
	}

}
