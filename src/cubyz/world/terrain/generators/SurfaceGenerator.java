package cubyz.world.terrain.generators;

import java.util.Random;

import cubyz.world.Chunk;
import cubyz.world.World;
import cubyz.world.blocks.Blocks;
import cubyz.world.terrain.Biome;
import cubyz.world.terrain.Generator;
import cubyz.world.terrain.MapGenerator;
/**
 * Generates the surface structure of the world.
 */

public class SurfaceGenerator implements Generator {
	int stone = Blocks.getByID("cubyz:stone");
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
		Random rand = new Random(world.seed);
		long xFactor = rand.nextLong() | 1;
		long zFactor = rand.nextLong() | 1;
		for(byte dx = 0; dx < Chunk.CHUNK_WIDTH; dx++) {
			int wx = dx*chunk.resolution + chunk.wx;
			for(byte dz = 0; dz < Chunk.CHUNK_WIDTH; dz++) {
				int wz = dz*chunk.resolution + chunk.wz;
				int mapX = (wx & MapGenerator.MAP_MASK)/mapResolution;
				int mapZ = (wz & MapGenerator.MAP_MASK)/mapResolution;
				int height = (int)heightMap[mapX][mapZ];
				height -= chunk.wy;
				height /= chunk.resolution;
				rand.setSeed(wx*xFactor + wz*zFactor);
				height = biomeMap[mapX][mapZ].blocks.generateColumn(chunk, dx, height, dz, rand);
				height = Math.min(31, height);
				for(byte dy = 0; dy <= height; dy++) {
					int index = Chunk.getIndex(dx, dy, dz);
					chunk.blocks[index] = stone;
				}
			}
		}
	}
	
}
