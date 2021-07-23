package cubyz.world.terrain.structures;

import cubyz.utils.datastructures.RegistryElement;

/**
 * An interface for all biome specific structures such as trees.
 */
public interface BiomeStructure extends RegistryElement {
	/**
	 * Creates a new structure model of the same type parsing the necessary parameters from a String.
	 * @param string
	 * @return
	 */
	public BiomeStructure readFromString(String string);
}
