package io.cubyz.world.blocks;

import java.util.Arrays;
import java.util.HashMap;

import io.cubyz.utils.datastructures.DataOrientedRegistry;
import io.cubyz.utils.json.JsonObject;
import io.cubyz.world.Registries;

/**
 * Stores data for all blocks.
 * Uses a data oriented design to improve cache locality and because it is much cleaner.
 * Without it there would need to be an additional byte array for the rotation data, while here it can easily be merged with the "pointer".
 * A Block is an integer that contains two parts:
 * 0x00ffffff : block type (index in this data structure).
 * 0xff000000 : rotation data(which gets interpreted by the rotation mode).
 */
public class Blocks implements DataOrientedRegistry {
	private static final int INITIAL_SIZE = 128;
	private static int size = 0;
	private static final int capacity = INITIAL_SIZE;

	private static String[] registryID = new String[INITIAL_SIZE];
	private static int[] blockClass = new int[INITIAL_SIZE];
	private static float[] hardness = new float[INITIAL_SIZE];
	private static int[] emission = new int[INITIAL_SIZE];
	private static int[] absorption = new int[INITIAL_SIZE];
	private static RotationMode[] rotationMode = new RotationMode[INITIAL_SIZE];
	private static boolean[] solid = new boolean[INITIAL_SIZE];
	private static boolean[] selectable = new boolean[INITIAL_SIZE];

	private static HashMap<String, Integer> reverseIndex = new HashMap<String, Integer>();

	/** stone, wood, … */
	public static int blockClass(int blockData) {
		return blockClass[blockData & 0xffffff];
	}
	/** How long it takes to break the block. (In seconds by hand) */
	public static float hardness(int blockData) {
		return hardness[blockData & 0xffffff];
	}

	/** How this will be accessed in the data files and in the console. */
	public static String registryID(int blockData) {
		return registryID[blockData & 0xffffff];
	}

	/** The amount of light that gets emitted. */
	public static int emission(int blockData) {
		return emission[blockData & 0xffffff];
	}

	/** The amount of light that gets absorbed. */
	public static int absorption(int blockData) {
		return absorption[blockData & 0xffffff];
	}
	
	/** How the block data gets interpreted. */
	public static RotationMode rotationMode(int blockData) {
		return rotationMode[blockData & 0xffffff];
	}

	/** Whether the block can interact with entities or fluids. */
	public static boolean solid(int blockData) {
		return solid[blockData & 0xffffff];
	}
	/** Whether the block can be selected by the player. */
	public static boolean selectable(int blockData) {
		return selectable[blockData & 0xffffff];
	}
	public static int getByID(String registryID) {
		return reverseIndex.get(registryID);
	}
	
	private static void ensureCapacity(int newCapacity) {
		if(newCapacity <= capacity) return;
		registryID = Arrays.copyOf(registryID, newCapacity);
		blockClass = Arrays.copyOf(blockClass, newCapacity);
		hardness = Arrays.copyOf(hardness, newCapacity);
		emission = Arrays.copyOf(emission, newCapacity);
		absorption = Arrays.copyOf(absorption, newCapacity);
		rotationMode = Arrays.copyOf(rotationMode, newCapacity);
		solid = Arrays.copyOf(solid, newCapacity);
		selectable = Arrays.copyOf(selectable, newCapacity);
	}

	@Override
	public void register(String registryID, JsonObject json) {
		if(size == capacity) {
			ensureCapacity(size*3/2);
		}
		System.out.println(registryID);
		Blocks.registryID[size] = registryID;
		blockClass[size] = json.getInt("blockClass", 0);
		hardness[size] = json.getFloat("hardness", 1);
		rotationMode[size] = Registries.ROTATION_MODES.getById(json.getString("rotationMode", "cubyz:no_rotation"));
		emission[size] = json.getInt("emission", 0);
		absorption[size] = json.getInt("absorption", 0);
		solid[size] = json.getBool("solid", true);
		selectable[size] = json.getBool("selectable", true);
	}

	@Override
	public void clear() {
		// Clear references:
		for(int i = 0; i < size; i++) {
			rotationMode[i] = null;
			registryID[i] = null;
		}
		// Clear reverse indexing:
		reverseIndex.clear();
		
		size = 0;
	}
	
	@Override
	public String getID() {
		return "cubyz:blocks";
	}
}
