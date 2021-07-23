package cubyz.world;

import java.util.Random;

import cubyz.world.blocks.Blocks;
import cubyz.world.terrain.MapGenerator;
import cubyz.world.terrain.generators.SurfaceGenerator;

/**
 * A Chunk how it's stored on the server. Contains data about every single block as well as the visibility data that is shared with the client.
 */

public class Chunk extends ChunkData {
	public static int CHUNK_SHIFT = 5;
	public static int CHUNK_SHIFT2 = 2*CHUNK_SHIFT;
	public static int CHUNK_WIDTH = 1 << CHUNK_SHIFT;
	public static int CHUNK_MASK = CHUNK_WIDTH - 1;
	public static int CHUNK_VOLUME = 1 << 3*CHUNK_SHIFT;
	
	/**
	 * Transforms a given xyz coordinate to the index in the block array.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int getIndex(int x, int y, int z) {
		return (x & CHUNK_MASK)<<CHUNK_SHIFT | (y & CHUNK_MASK) | (z & CHUNK_MASK)<<CHUNK_SHIFT2;
	}
	
	
	
	/** Block data */
	public final int[] blocks = new int[CHUNK_VOLUME];
	
	public Chunk(ChunkData data) {
		super(data);
		generate();
	}
	
	public Chunk(World world, int wx, int wy, int wz, int resolution) {
		super(world, wx, wy, wz, resolution);
		generate();
	}
	private void generate() {
		MapGenerator map = ChunkCache.getOrGenerateMap(world, wx, wz);
		map.ensureResolution(resolution);
		new SurfaceGenerator().generate(world, this, map);
	}
}
