package cubyz.world;

import cubyz.utils.datastructures.Cache;
import cubyz.world.terrain.MapGenerator;
import cubyz.world.terrain.generators.SurfaceGenerator;

/**
 * Chunks that get requested by users are cached in case they are needed again.
 * 
 * Implements a simple set associative cache with LRU replacement strategy.
 */


public class ChunkCache {
	/** How many chunks there can be for each hash. */
	public static final int ASSOCIATIVITY = 4;
	public static final int BIT_SIZE_CHUNK = 10;
	/** How many blocks of size ASSOCIATIVITY there are in this cache. */
	public static final int SIZE_CHUNK = 1 << BIT_SIZE_CHUNK;
	public static final int HASH_MASK_CHUNK = SIZE_CHUNK-1;
	
	public static final int BIT_SIZE_VIS = 12;
	/** How many blocks of size ASSOCIATIVITY there are in this cache. */
	public static final int SIZE_VIS = 1 << BIT_SIZE_VIS;
	public static final int HASH_MASK_VIS = SIZE_VIS-1;
	
	public static final int BIT_SIZE_MAP = 8;
	/** How many blocks of size ASSOCIATIVITY there are in this cache. */
	public static final int SIZE_MAP = 1 << BIT_SIZE_MAP;
	public static final int HASH_MASK_MAP = SIZE_MAP-1;
	public static final Cache<Chunk> chunkCache = new Cache<Chunk>(new Chunk[SIZE_CHUNK][ASSOCIATIVITY]);
	public static final Cache<ChunkVisibilityData> visibilityDataCache = new Cache<ChunkVisibilityData>(new ChunkVisibilityData[SIZE_VIS][ASSOCIATIVITY]);
	public static final Cache<MapGenerator> mapGeneratorCache = new Cache<MapGenerator>(new MapGenerator[SIZE_MAP][ASSOCIATIVITY]);
	
	public static MapGenerator getOrGenerateMap(World world, int wx, int wz) {
		wx &= ~MapGenerator.MAP_MASK;
		wz &= ~MapGenerator.MAP_MASK;
		ChunkData request = new ChunkData(world, wx, 0, wz, -1);
		int hash = request.hashCode() & HASH_MASK_MAP;
		// Check if that chunk is in the cache:
		MapGenerator ret = mapGeneratorCache.find(request, hash);
		if(ret != null) return ret;
		// Create the new chunk:
		synchronized(mapGeneratorCache.cache[hash]) {
			// First of all check again if the searched chunk was maybe just generated while this thread was waiting:
			ret = mapGeneratorCache.find(request, hash);
			if(ret != null) return ret;
			// Otherwise create the chunk and put it in the cache:
			ret = new MapGenerator(request.world, request.wx, request.wz);
			mapGeneratorCache.addToCache(ret, hash);
			return ret;
		}
	}
	
	public static Chunk getOrGenerateChunk(ChunkData request) {
		int hash = request.hashCode() & HASH_MASK_CHUNK;
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
		int hash = request.hashCode() & HASH_MASK_VIS;
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
	
	public static ChunkVisibilityData getOrNull(ChunkData request) {
		int hash = request.hashCode() & HASH_MASK_VIS;
		// Check if that chunk is in the cache:
		ChunkVisibilityData ret = visibilityDataCache.find(request, hash);
		return ret;
	}
}
