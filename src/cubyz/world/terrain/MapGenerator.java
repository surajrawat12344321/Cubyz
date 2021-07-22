package cubyz.world.terrain;

import java.awt.image.BufferedImage;
import java.util.Random;

import cubyz.utils.Utils;
import cubyz.world.World;
import cubyz.world.biomes.Biome;
import cubyz.world.terrain.noise.FractalNoise;

/**
 * Generates the height and Biome maps of the planet.
 */
public class MapGenerator {
	private static class BiomePoint {
		final Biome biome;
		final float x;
		final float z;
		public BiomePoint(Biome biome, float x, float z) {
			this.biome = biome;
			this.x = x;
			this.z = z;
		}
		float distSquare(float x, float z) {
			return (this.x - x)*(this.x - x) + (this.z - z)*(this.z - z);
		}
		float maxNorm(float x, float z) {
			return Math.max(Math.abs(x - this.x), Math.abs(z - this.z));
		}
	}
	public static final int BIOME_SHIFT = 7;
	/** The average diameter of a biome. */
	public static final int BIOME_SIZE = 1 << BIOME_SHIFT;
	public static final int MAP_SHIFT = 10;
	public static final int MAP_SIZE = 1 << MAP_SHIFT;
	public final World world;
	public final int wx, wz;
	private int resolution = Integer.MAX_VALUE;
	private float[][] heightMap;
	private Biome[][] biomeMap;
	public MapGenerator(World world, int wx, int wz, int resolution) {
		this.world = world;
		this.wx = wx;
		this.wz = wz;
		
		ensureResolution(resolution);
	}
	
	public void ensureResolution(int resolution) {
		synchronized(this) {
			if(resolution < this.resolution) {
				int scaledSize = MAP_SIZE/resolution;
				this.resolution = resolution;
				heightMap = new float[scaledSize][scaledSize];
				biomeMap = new Biome[scaledSize][scaledSize];
				// Create the biomes that will be placed on the map:
				BiomePoint[][] biomePositions = new BiomePoint[MAP_SIZE/BIOME_SIZE + 3][MAP_SIZE/BIOME_SIZE + 3];
				Random rand = new Random();
				for(int x = -BIOME_SIZE; x <= MAP_SIZE + BIOME_SIZE; x += BIOME_SIZE) {
					for(int z = -BIOME_SIZE; z <= MAP_SIZE + BIOME_SIZE; z += BIOME_SIZE) {
						rand.setSeed((x + wx)*65784967549L + (z + wz)*6758934659L + world.seed);
						// TODO: Use Biomes from registry.
						biomePositions[x/BIOME_SIZE + 1][z/BIOME_SIZE + 1] = new BiomePoint(new Biome(""), (x + rand.nextInt(BIOME_SIZE) - BIOME_SIZE/2)/(float)resolution, (z + rand.nextInt(BIOME_SIZE) - BIOME_SIZE/2)/(float)resolution);
					}
				}
				int scaledBiomeSize = BIOME_SIZE/resolution;
				float[][] xOffsetMap = new float[scaledSize][scaledSize];
				float[][] zOffsetMap = new float[scaledSize][scaledSize];
				FractalNoise.generateSparseFractalTerrain(wx, wz, MAP_SIZE, MAP_SIZE, BIOME_SIZE/2, world.seed^675396758496549L, world.sizeX, world.sizeZ, xOffsetMap, resolution);
				FractalNoise.generateSparseFractalTerrain(wx, wz, MAP_SIZE, MAP_SIZE, BIOME_SIZE/2, world.seed^543864367373859L, world.sizeX, world.sizeZ, zOffsetMap, resolution);
				BufferedImage img = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_ARGB);
				for(int x = 0; x < heightMap.length; x++) {
					for(int z = 0; z < heightMap.length; z++) {
						// Do the biome interpolation:
						float totalWeight = 0;
						float r = 0;
						float g = 0;
						float b = 0;
						int xBiome = (x + scaledBiomeSize/2)/scaledBiomeSize;
						int zBiome = (z + scaledBiomeSize/2)/scaledBiomeSize;
						for(int x0 = xBiome; x0 <= xBiome+2; x0++) {
							for(int z0 = zBiome; z0 <= zBiome+2; z0++) {
								float dist = (float)Math.sqrt(biomePositions[x0][z0].distSquare(x, z));
								dist /= scaledBiomeSize;
								float maxNorm = biomePositions[x0][z0].maxNorm(x, z)/scaledBiomeSize;
								// There are cases where this point is further away than 1 unit from all nearby biomes. For that case the euclidian distance function is interpolated to the max-norm for higher distances.
								if(dist > 0.9f && maxNorm < 1) {
									float borderMax = 0.9f*maxNorm/dist;
									float scale = 1/(1 - borderMax);
									dist = dist*(1 - maxNorm)*scale + scale*(maxNorm - borderMax)*maxNorm;
								}
								// Biomes only add to the map when they are closer than the 
								if(dist <= 1) {
									float weight = (1 - dist);
									// smooth the interpolation with the s-curve:
									weight = weight*weight*(3 - 2*weight);
									int rgb = biomePositions[x0][z0].biome.hashCode();
									r += (rgb>>16 & 255)*weight;
									g += (rgb>>8 & 255)*weight;
									b += (rgb>>0 & 255)*weight;
									totalWeight += weight;
								}
							}
						}
						// Norm the result:
						r /= totalWeight;
						g /= totalWeight;
						b /= totalWeight;
						// TODO: Generate height.

						// Select a biome. The shape of the biome is randomized by applying noise (fractal noise and white noise) to the coordinates.
						float updatedX = x + (rand.nextInt(8) - 3.5f)*scaledBiomeSize/128 + (xOffsetMap[x][z] - 0.5f)*scaledBiomeSize/2;
						float updatedZ = z + (rand.nextInt(8) - 3.5f)*scaledBiomeSize/128 + (zOffsetMap[x][z] - 0.5f)*scaledBiomeSize/2;
						xBiome = (int)(updatedX + scaledBiomeSize/2)/scaledBiomeSize;
						zBiome = (int)(updatedZ + scaledBiomeSize/2)/scaledBiomeSize;
						float shortestDist = Float.MAX_VALUE;
						Biome shortestBiome = null;
						for(int x0 = xBiome; x0 <= xBiome+2; x0++) {
							for(int z0 = zBiome; z0 <= zBiome+2; z0++) {
								float distSquare = biomePositions[x0][z0].distSquare(updatedX, updatedZ);
								if(distSquare < shortestDist) {
									shortestDist = distSquare;
									shortestBiome = biomePositions[x0][z0].biome;
								}
							}
						}
						biomeMap[x][z] = shortestBiome;
						img.setRGB(x, z, shortestBiome.hashCode() | 0xff000000);
					}
				}
				Utils.writeImage(img, "test.png");
			}
		}
	}
}
