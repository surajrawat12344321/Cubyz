package cubyz.world.terrain;

import cubyz.utils.datastructures.RegistryElement;
import cubyz.world.Chunk;
import cubyz.world.World;

/**
 * Some interface to access all different generators(caves, terrain, …).
 */

public interface Generator extends RegistryElement {
	/** Used to prioritize certain generators(like map generation) over others(like vegetation generation). */
	abstract int getPriority(); 
	/**
	 * Generates a given Chunk.
	 * @param world
	 * @param wx
	 * @param wy
	 * @param wz
	 * @param chunk
	 * @param containingRegion
	 * @param surface
	 */
	abstract void generate(World world, int wx, int wy, int wz, Chunk chunk, MapGenerator heightMap);
}