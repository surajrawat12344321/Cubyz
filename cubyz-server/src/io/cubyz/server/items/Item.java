package io.cubyz.server.items;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * TODO
 */
public class Item implements RegistryElement {
	private final String ID;
	
	public Item(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String getID() {
		return ID;
	}

}
