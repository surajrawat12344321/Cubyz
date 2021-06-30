package io.meshes;

import java.util.Arrays;

import io.cubyz.renderUniverse.TextureAtlas;
import io.cubyz.utils.Utils;
import io.cubyz.utils.datastructures.DataOrientedRegistry;
import io.cubyz.utils.json.JsonObject;

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

	private static void ensureCapacity(int newCapacity) {
		if(newCapacity <= capacity) return;
		atlasPositions = Arrays.copyOf(atlasPositions, newCapacity*4);
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
		int[] positions = TextureAtlas.BLOCKS.addTexture(Utils.readImage(Utils.idToFile(json.getString("texture", "cubyz:default"), "blocks/textures", ".png")));
		System.arraycopy(positions, 0, atlasPositions, size*4, 4);
		size++;
	}
	
}
