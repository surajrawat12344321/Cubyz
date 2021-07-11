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
	public boolean equals(ChunkData other) {
		return other != null && other.world == world && other.wx == wx && other.wy == wy && other.wz == wz && other.resolution == resolution;
	}
}
