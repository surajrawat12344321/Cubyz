package cubyz.client.meshes;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import cubyz.client.renderUniverse.TextureAtlas;
import cubyz.utils.FileUtils;
import cubyz.utils.datastructures.DataOrientedRegistry;
import cubyz.utils.json.JsonObject;

/**
 * Stores all the mesh data for the blocks. Uses the same ID-ing as `Blocks`.
 * TODO.
 */

public class BlockMeshes implements DataOrientedRegistry {
	private static final int INITIAL_SIZE = 128;
	private static int size = 1; // The initial size is 1, which is reserved for air.
	private static final int capacity = INITIAL_SIZE;
	
	/** Contains the atlas positons as consecutive x,y,width,height. */
	private static int[] atlasPositions = new int[4*INITIAL_SIZE];
	private static boolean[] opaque = new boolean[INITIAL_SIZE];
	private static boolean[] transparent = new boolean[INITIAL_SIZE];
	
	/**
	 * x Position of the texture of this block on the atlas
	 * @param blockData
	 * @return x
	 */
	public static int atlasX(int blockData) {
		return atlasPositions[4*(blockData & 0xffffff)];
	}
	
	/**
	 * y Position of the texture of this block on the atlas
	 * @param blockData
	 * @return y
	 */
	public static int atlasY(int blockData) {
		return atlasPositions[4*(blockData & 0xffffff) + 1];
	}
	
	/**
	 * width of the texture of this block on the atlas
	 * @param blockData
	 * @return width
	 */
	public static int atlasWidth(int blockData) {
		return atlasPositions[4*(blockData & 0xffffff) + 2];
	}
	
	/**
	 * height of the texture of this block on the atlas
	 * @param blockData
	 * @return height
	 */
	public static int atlasHeight(int blockData) {
		return atlasPositions[4*(blockData & 0xffffff) + 3];
	}
	
	/**
	 * Returns true if the textures contains opaque pixels.
	 * @param blockData
	 * @return opaque
	 */
	public static boolean opaque(int blockData) {
		return opaque[blockData & 0xffffff];
	}
	
	/**
	 * Returns true if the textures contains partly transparent pixels.
	 * @param blockData
	 * @return transparent
	 */
	public static boolean transparent(int blockData) {
		return transparent[blockData & 0xffffff];
	}

	private static void ensureCapacity(int newCapacity) {
		if(newCapacity <= capacity) return;
		atlasPositions = Arrays.copyOf(atlasPositions, newCapacity*4);
		opaque = Arrays.copyOf(opaque, newCapacity);
		transparent = Arrays.copyOf(transparent, newCapacity);
	}
	
	@Override
	public String getID() {
		return "cubyz:block_meshes";
	}

	@Override
	public void clear() {
		size = 1; // The initial size is 1, which is reserved for air.
	}
	@Override
	public void register(String registryID, JsonObject json) {
		if(size == capacity) {
			ensureCapacity(size*3/2);
		}
		BufferedImage image = FileUtils.readImage(FileUtils.idToFile(json.getString("texture", "cubyz:default"), "blocks/textures", ".png"));
		opaque[size] = false;
		transparent[size] = false;
		// Check if any of the pixels are opaque or transparent:
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				int alpha = image.getRGB(x, y)>>>24 & 0xff;
				if(alpha == 0xff) {
					opaque[size] = true;
				} else if(alpha != 0x00) {
					transparent[size] = true;
				}
			}
		}
		int[] positions = TextureAtlas.BLOCKS.addTexture(image);
		System.arraycopy(positions, 0, atlasPositions, size*4, 4);
		size++;
	}
	
}
