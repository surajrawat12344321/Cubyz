package cubyz.world;

/**
 * Stores one World of the universe(for example a planet).
 * TODO
 */

public class World {
	public final int worldID = 0;
	public final int sizeX = 131072;
	public final int sizeZ = 32768;
	public final long seed = 6487396473896L;
	
	/**
	 * Transforms world coordinates into a looping coordinate system to make the world wrap around at the borders.
	 * @param value
	 * @param worldSize
	 * @return
	 */
	public static float worldModulo(float value, int worldSize) {
		if(value < 0) return value%worldSize + worldSize;
		return value%worldSize;
	}
	/**
	 * Transforms world coordinates into a looping coordinate system to make the world wrap around at the borders.
	 * @param value
	 * @param worldSize
	 * @return
	 */
	public static int worldModulo(int value, int worldSize) {
		if(value < 0) return value%worldSize + worldSize;
		return value%worldSize;
	}
}
