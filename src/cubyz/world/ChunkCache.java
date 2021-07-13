package cubyz.world;

import cubyz.utils.datastructures.Cache;

/**
 * Chunks that get requested by users are cached in case they are needed again.
 * 
 * Implements a simple set associative cache with LRU replacement strategy.
 */


public class ChunkCache {
	/** How many chunks there can be for each hash. */
	public static final int ASSOCIATIVITY = 4;
	public static final int BIT_SIZE = 10;
	/** How many blocks of size ASSOCIATIVITY there are in this cache. */
	public static final int SIZE = 1 << BIT_SIZE;
	public static final int HASH_MASK = SIZE-1;
	public static final Cache<Chunk> chunkCache = new Cache<Chunk>(new Chunk[SIZE][ASSOCIATIVITY]);
	public static final Cache<ChunkVisibilityData> visibilityDataCache = new Cache<ChunkVisibilityData>(new ChunkVisibilityData[SIZE][ASSOCIATIVITY]);
	
	public static Chunk getOrGenerateChunk(ChunkData request) {
		int hash = request.hashCode() & HASH_MASK;
		// Check if that chunk is in the cache:
		Chunk ret = chunkCache.find(request, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(chunkCache.cache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = chunkCache.find(request, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new Chunk(request);
			chunkCache.addToCache(ret, hash);
			return ret;
		}
	}
	
	public static ChunkVisibilityData getOrGenerateVisibilityData(ChunkData request) {
		int hash = request.hashCode() & HASH_MASK;
		// Check if that chunk is in the cache:
		ChunkVisibilityData ret = visibilityDataCache.find(request, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(visibilityDataCache.cache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = visibilityDataCache.find(request, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new ChunkVisibilityData(getOrGenerateChunk(request));
			visibilityDataCache.addToCache(ret, hash);
			return ret;
		}
	}
}
