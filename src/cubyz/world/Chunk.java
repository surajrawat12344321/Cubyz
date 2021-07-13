package cubyz.world;

import java.util.Random;

import cubyz.world.blocks.Blocks;

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
		// TODO: Remove test.
		Random rand = new Random(wx*65783906349L ^ wz*6758496543365421L);
		float val00 = rand.nextFloat()*92 - 64;
		rand = new Random(wx*65783906349L ^ (wz + Chunk.CHUNK_WIDTH*resolution)*6758496543365421L);
		float val01 = rand.nextFloat()*92 - 64;
		rand = new Random((wx + Chunk.CHUNK_WIDTH*resolution)*65783906349L ^ wz*6758496543365421L);
		float val10 = rand.nextFloat()*92 - 64;
		rand = new Random((wx + Chunk.CHUNK_WIDTH*resolution)*65783906349L ^ (wz + Chunk.CHUNK_WIDTH*resolution)*6758496543365421L);
		float val11 = rand.nextFloat()*92 - 64;
		int block;
		rand.setSeed((wx + Chunk.CHUNK_WIDTH*resolution)*65783906349L ^ wy ^ (wz + Chunk.CHUNK_WIDTH*resolution)*6758496543365421L);
		do {
			block = 1+(int)(rand.nextFloat()*(Blocks.size()-1));
		} while(!Blocks.model(block).isCube);
		for(byte dx = 0; dx < CHUNK_WIDTH; dx++) {
			for(byte dz = 0; dz < CHUNK_WIDTH; dz++) {
				float height = dx*dz*val11/Chunk.CHUNK_WIDTH/Chunk.CHUNK_WIDTH
						+ dx*(Chunk.CHUNK_WIDTH-dz)*val10/Chunk.CHUNK_WIDTH/Chunk.CHUNK_WIDTH
						+ (Chunk.CHUNK_WIDTH-dx)*dz*val01/Chunk.CHUNK_WIDTH/Chunk.CHUNK_WIDTH
						+ (Chunk.CHUNK_WIDTH-dx)*(Chunk.CHUNK_WIDTH-dz)*val00/Chunk.CHUNK_WIDTH/Chunk.CHUNK_WIDTH;
				height -= wy;
				height = Math.min(32, height/resolution);
				for(byte dy = 0; dy < height; dy++) {
					int index = getIndex(dx, dy, dz);
					blocks[index] = block;
				}
			}
		}
	}
}
