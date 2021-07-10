package io.cubyz.world;
/**
 * Chunks that get requested by users are cached in case they are needed again.
 * 
 * Implements a simple set associative cache with LRU replacement strategy.
 */


public class ChunkCache {
	/** How many chunks there can be for each hash. */
	public static final int ASSOCIATIVITY = 4;
	public static final int BIT_SIZE = 8;
	/** How many blocks of size ASSOCIATIVITY there are in this cache. */
	public static final int SIZE = 1 << BIT_SIZE;
	public static final int HASH_MASK = SIZE-1;
	private static final Chunk[][] chunkCache = new Chunk[SIZE][ASSOCIATIVITY];
	private static final ChunkVisibilityData[][] visibilityDataCache = new ChunkVisibilityData[SIZE][ASSOCIATIVITY];
	
	private static Chunk findChunkInCache(World world, int x, int y, int z, int resolution, int hash) {
		for(int i = 0; i < ASSOCIATIVITY; i++) {
			Chunk ret = chunkCache[hash][i];
			if(ret != null && ret.world == world && ret.wx == x && ret.wy == y && ret.wz == z && ret.resolution == resolution) {
				if(i != 0) { // No need to put it up front when it already is on the front.
					synchronized(chunkCache[hash]) {
						System.arraycopy(chunkCache[hash], 0, chunkCache[hash], 1, i);
						chunkCache[hash][i] = ret;
					}
				}
				return ret;
			}
		}
		return null;
	}
	
	public static int cacheRequests = 0;
	public static int cacheMisses = 0;
	
	public static Chunk getChunk(World world, int x, int y, int z, int resolution) {
		cacheRequests++;
		// Transform to valid coordinates:
		int cx = x>>Chunk.CHUNK_SHIFT;
		int cy = y>>Chunk.CHUNK_SHIFT;
		int cz = z>>Chunk.CHUNK_SHIFT;
		int hash = world.worldID ^ cx<<2 ^ cx>>>30 ^ cy<<4 ^ cy>>>28 ^ cz<<6 ^ cz>>>28;
		hash &= HASH_MASK;
		// Check if that chunk is in the cache:
		Chunk ret = findChunkInCache(world, x, y, z, resolution, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(chunkCache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = findChunkInCache(world, x, y, z, resolution, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new Chunk(world, x, y, z, resolution);
			System.arraycopy(chunkCache[hash], 0, chunkCache[hash], 1, 3);
			chunkCache[hash][0] = ret;
			cacheMisses++;
			return ret;
		}
	}
	
	private static ChunkVisibilityData findVisibilityDataInCache(World world, int x, int y, int z, int resolution, int hash) {
		for(int i = 0; i < ASSOCIATIVITY; i++) {
			ChunkVisibilityData ret = visibilityDataCache[hash][i];
			if(ret != null && ret.world == world && ret.wx == x && ret.wy == y && ret.wz == z && ret.resolution == resolution) {
				if(i != 0) { // No need to put it up front when it already is on the front.
					synchronized(visibilityDataCache[hash]) {
						System.arraycopy(visibilityDataCache[hash], 0, visibilityDataCache[hash], 1, i);
						visibilityDataCache[hash][i] = ret;
					}
				}
				return ret;
			}
		}
		return null;
	}
	
	public static ChunkVisibilityData getVisibilityData(World world, int x, int y, int z, int resolution) {
		// Transform to valid coordinates:
		int cx = x>>Chunk.CHUNK_SHIFT;
		int cy = y>>Chunk.CHUNK_SHIFT;
		int cz = z>>Chunk.CHUNK_SHIFT;
		int hash = world.worldID ^ cx<<2 ^ cx>>>30 ^ cy<<4 ^ cy>>>28 ^ cz<<6 ^ cz>>>28;
		hash &= HASH_MASK;
		// Check if that chunk is in the cache:
		ChunkVisibilityData ret = findVisibilityDataInCache(world, x, y, z, resolution, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(visibilityDataCache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = findVisibilityDataInCache(world, x, y, z, resolution, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new ChunkVisibilityData(getChunk(world, x, y, z, resolution));
			System.arraycopy(visibilityDataCache[hash], 0, visibilityDataCache[hash], 1, 3);
			visibilityDataCache[hash][0] = ret;
			return ret;
		}
	}
}
