package cubyz.world.terrain.generators;

import cubyz.world.Chunk;
import cubyz.world.World;
import cubyz.world.terrain.Biome;
import cubyz.world.terrain.Generator;
import cubyz.world.terrain.MapGenerator;
/**
 * Generates the surface structure of the world.
 */

public class SurfaceGenerator implements Generator {

	@Override
	public String getID() {
		return "cubyz:surface";
	}

	@Override
	public int getPriority() {
		return 1000000;
	}

	@Override
	public void generate(World world, Chunk chunk, MapGenerator map) {
		float[][] heightMap;
		Biome[][] biomeMap;
		int mapResolution;
		synchronized(map) {
			heightMap = map.heightMap;
			biomeMap = map.biomeMap;
			mapResolution = map.resolution;
		}
		for(byte dx = 0; dx < Chunk.CHUNK_WIDTH; dx++) {
			for(byte dz = 0; dz < Chunk.CHUNK_WIDTH; dz++) {
				float height = heightMap[(dx*chunk.resolution + chunk.wx & MapGenerator.MAP_MASK)/mapResolution][(dz*chunk.resolution + chunk.wz & MapGenerator.MAP_MASK)/mapResolution];
				height -= chunk.wy;
				height = Math.min(32, height/chunk.resolution);
				for(byte dy = 0; dy < height; dy++) {
					int index = Chunk.getIndex(dx, dy, dz);
					chunk.blocks[index] = 1;
				}
			}
		}
	}
	
}
