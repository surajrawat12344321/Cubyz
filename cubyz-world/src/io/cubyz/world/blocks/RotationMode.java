package io.cubyz.world.blocks;

import io.cubyz.utils.datastructures.RegistryElement;


/**
 * TODO!
 */
public interface RotationMode extends RegistryElement {
	/**
	 * Creates a new RotationMode instance for the specified model.
	 * @param modelID Contains mod name and model name sperated by ":". The .obj ending will be appended automatically.
	 * @return
	 */
	public RotationMode createModel(String modelID);
	
	/**
	 * Gets the model for a specified block.
	 * @param blockData
	 * @return
	 */
	public Model getModel(int blockData);
}
