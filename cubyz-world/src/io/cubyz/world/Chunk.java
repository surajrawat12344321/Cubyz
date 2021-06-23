package io.cubyz.world;

/**
 * A Chunk how it's stored on the server. Contains data about every single block as well as the visibility data that is shared with the client.
 */

public class Chunk {
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
	
	
	
	/** World coordinates */
	public final int wx, wy, wz;
	/** The base unit of one block inside this chunk. */
	public final int resolution;
	/** Block data */
	public final int[] blocks = new int[CHUNK_VOLUME];
	
	public Chunk(int wx, int wy, int wz, int resolution) {
		this.wx = wx;
		this.wy = wy;
		this.wz = wz;
		this.resolution = resolution;
	}
}
