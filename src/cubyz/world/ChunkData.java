package cubyz.world;

/**
 * Contains all the data used by Chunks and similar.
 */
public class ChunkData {/** World coordinates */
	public final int wx, wy, wz;
	/** The base unit of one block inside this chunk. */
	public final int resolution;
	/** The world this chunk is in. */
	public final World world;
	public ChunkData(World world, int wx, int wy, int wz, int resolution) {
		this.world = world;
		this.wx = wx;
		this.wy = wy;
		this.wz = wz;
		this.resolution = resolution;
	}
	public ChunkData(ChunkData other) {
		this.world = other.world;
		this.wx = other.wx;
		this.wy = other.wy;
		this.wz = other.wz;
		this.resolution = other.resolution;
	}
	
	/**
	 * Compares the data of the input with this.
	 * Different data holders with the same chunk data are handles as equals.
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof ChunkData
				&& ((ChunkData)other).world == world
				&& ((ChunkData)other).wx == wx
				&& ((ChunkData)other).wy == wy
				&& ((ChunkData)other).wz == wz
				&& ((ChunkData)other).resolution == resolution;
	}
	
	@Override
	public int hashCode() {
		int cx = wx>>Chunk.CHUNK_SHIFT;
		int cy = wy>>Chunk.CHUNK_SHIFT;
		int cz = wz>>Chunk.CHUNK_SHIFT;
		return world.worldID ^ cx ^ cx<<4 ^ cy ^ cy<<2 ^ cz ^ cz<<5;
	}
}
