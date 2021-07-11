package cubyz.world;

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
	
	private static Chunk findChunkInCache(ChunkData request, int hash) {
		for(int i = 0; i < ASSOCIATIVITY; i++) {
			Chunk ret = chunkCache[hash][i];
			if(request.equals(ret)) {
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
	
	public static Chunk getChunk(ChunkData request) {
		cacheRequests++;
		// Transform to valid coordinates:
		int cx = request.wx>>Chunk.CHUNK_SHIFT;
		int cy = request.wy>>Chunk.CHUNK_SHIFT;
		int cz = request.wz>>Chunk.CHUNK_SHIFT;
		int hash = request.world.worldID ^ cx<<2 ^ cx>>>30 ^ cy<<4 ^ cy>>>28 ^ cz<<6 ^ cz>>>28;
		hash &= HASH_MASK;
		// Check if that chunk is in the cache:
		Chunk ret = findChunkInCache(request, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(chunkCache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = findChunkInCache(request, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new Chunk(request);
			System.arraycopy(chunkCache[hash], 0, chunkCache[hash], 1, 3);
			chunkCache[hash][0] = ret;
			cacheMisses++;
			return ret;
		}
	}
	
	private static ChunkVisibilityData findVisibilityDataInCache(ChunkData request, int hash) {
		for(int i = 0; i < ASSOCIATIVITY; i++) {
			ChunkVisibilityData ret = visibilityDataCache[hash][i];
			if(request.equals(ret)) {
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
	
	public static ChunkVisibilityData getVisibilityData(ChunkData request) {
		// Transform to valid coordinates:
		int cx = request.wx>>Chunk.CHUNK_SHIFT;
		int cy = request.wy>>Chunk.CHUNK_SHIFT;
		int cz = request.wz>>Chunk.CHUNK_SHIFT;
		int hash = request.world.worldID ^ cx<<2 ^ cx>>>30 ^ cy<<4 ^ cy>>>28 ^ cz<<6 ^ cz>>>28;
		hash &= HASH_MASK;
		// Check if that chunk is in the cache:
		ChunkVisibilityData ret = findVisibilityDataInCache(request, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(visibilityDataCache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = findVisibilityDataInCache(request, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new ChunkVisibilityData(getChunk(request));
			System.arraycopy(visibilityDataCache[hash], 0, visibilityDataCache[hash], 1, 3);
			visibilityDataCache[hash][0] = ret;
			return ret;
		}
	}
}
