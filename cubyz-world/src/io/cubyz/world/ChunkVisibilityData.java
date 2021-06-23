package io.cubyz.world;

import java.util.Arrays;

import io.cubyz.world.blocks.Blocks;

/**
 * Contains data about all the visible faces of a Chunk.
 * Internally its organized using an ArrayList-like structure.
 * Even in worst case (Every second block of the chunk filled) sending this over to the client will be cheaper than sending the whole chunk.
 */

public class ChunkVisibilityData {
	private static final int INITIAL_CAPACITY = 128;
	/** World position of the Chunk. */
	public final int wx, wy, wz;
	/** The base unit of one block inside this chunk. */
	public final int resolution;
	/** The number of visible blocks in this Chunk.*/
	public int size = 0, capacity = INITIAL_CAPACITY;
	/** The relative position data for each visible block */
	byte[] x = new byte[INITIAL_CAPACITY], y = new byte[INITIAL_CAPACITY], z = new byte[INITIAL_CAPACITY];
	/** The block data for each visible block */
	int[] blocks = new int[INITIAL_CAPACITY];
	/** Densely packed information about what neighbors are solid, to reduce amount of faces drawn. */
	byte[] neighbors = new byte[INITIAL_CAPACITY];
	
	public ChunkVisibilityData(Chunk source, Chunk[] neighbors) {
		wx = source.wx;
		wy = source.wy;
		wz = source.wz;
		resolution = source.resolution;
		for(byte rx = 0; rx < Chunk.CHUNK_WIDTH; rx++) {
			for(byte rz = 0; rz < Chunk.CHUNK_WIDTH; rz++) {
				for(byte ry = 0; ry < Chunk.CHUNK_WIDTH; ry++) {
					int index = Chunk.getIndex(rx, ry, rz);
					int block = source.blocks[index];
					byte neighborData = 0;
					for(int neighbor = 0; neighbor < Neighbor.NEIGHBORS; neighbor++) {
						Chunk chunk = source;
						int rx2 = rx + Neighbor.REL_X[neighbor];
						int ry2 = ry + Neighbor.REL_Y[neighbor];
						int rz2 = rz + Neighbor.REL_Z[neighbor];
						if(rx2 < 0 | ry2 < 0 | rz2 < 0 | rx2 >= 16 | ry2 >= 16 | rz2 >= 16) {
							chunk = neighbors[neighbor];
						}
						int index2 = Chunk.getIndex(rx2, ry2, rz2);
						int block2 = chunk.blocks[index2];
						if(!Blocks.getsBlocked(block, block2)) {
							neighborData |= Neighbor.BIT_MASK[neighbor];
						}
					}
					if(neighborData != 0) {
						addBlock_noChecks(rx, ry, rz, block, neighborData);
					}
				} 
			}
		}
	}
	
	/**
	 * Adds a block to the list of visible blocks. Doesn't check if the block is already present!
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param neighbors
	 */
	public void addBlock_noChecks(byte x, byte y, byte z, int block, byte neighbors) {
		if(size == capacity)
			increaseCapacity();
		this.x[size] = x;
		this.y[size] = y;
		this.z[size] = z;
		this.blocks[size] = block;
		this.neighbors[size] = neighbors;
		size++;
	}
	
	/**
	 * Removes a block making it invisible. Used on block updates.
	 * Assumes there are no duplicate entries.
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param neighbors
	 */
	public void removeBlock(byte x, byte y, byte z) {
		size--;
		for(int i = 0; i < size; i++) {
			if(this.x[i] == x && this.y[i] == y && this.z[i] == z) {
				// Fill the hole by taking the last in the list.
				this.x[i] = this.x[size];
				this.y[i] = this.y[size];
				this.z[i] = this.z[size];
				blocks[i] = blocks[size];
				neighbors[i] = neighbors[size];
			}
		}
	}
	
	private void increaseCapacity() {
		capacity *= 2;
		x = Arrays.copyOf(x, capacity);
		y = Arrays.copyOf(y, capacity);
		z = Arrays.copyOf(z, capacity);
		blocks = Arrays.copyOf(blocks, capacity);
		neighbors = Arrays.copyOf(neighbors, capacity);
	}
}
