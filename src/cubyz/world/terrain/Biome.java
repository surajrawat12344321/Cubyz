package cubyz.world.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.function.Consumer;

import cubyz.utils.datastructures.RegistryElement;
import cubyz.utils.datastructures.random.ChanceObject;
import cubyz.utils.datastructures.random.RandomList;
import cubyz.utils.log.Log;
import cubyz.world.terrain.structures.BlockStructure;
import cubyz.world.Registries;
import cubyz.world.terrain.structures.BiomeStructure;

/**
 * Biomes are used to determine the shape of the surface of the world.
 */
public class Biome implements RegistryElement, ChanceObject {
	public static enum Type {
		/**hot, wet, lowland*/
		RAINFOREST,
		/**hot, medium, lowland*/
		SHRUBLAND,
		/**hot, dry, lowland*/
		DESERT,
		/**temperate, wet, lowland*/
		SWAMP,
		/**temperate, medium, lowland*/
		FOREST,
		/**temperate, dry, lowland*/
		GRASSLAND,
		/**cold, icy, lowland*/
		TUNDRA,
		/**cold, medium, lowland*/
		TAIGA,
		/**cold, icy, lowland*/
		GLACIER,
		

		/**temperate, medium, highland*/
		MOUNTAIN_FOREST,
		/**temperate, dry, highland*/
		MOUNTAIN_GRASSLAND,
		/**cold, dry, highland*/
		PEAK,
		

		/**temperate ocean*/
		OCEAN,
		/**tropical ocean(coral reefs and stuff)*/
		WARM_OCEAN,
		/**arctic ocean(ice sheets)*/
		ARCTIC_OCEAN,
		
		/**deep ocean trench*/
		TRENCH,
		
		
		/**region that never sees the sun, due to how the torus orbits it.*/
		ETERNAL_DARKNESS,
	}
	
	public final Type type;
	/** Minimum and maximum height this biome is allowed to have. */
	public final float minHeight, maxHeight;
	/** Local terrain roughness */
	public final float roughness;
	/** Medium sized bumps in terrain */
	public final float hills;
	/** For mountains with rough peaks. */
	public final float mountains;
	/** How likely it spawns naturally. */
	public final int chance;
	public final String ID;
	/** Surface block structure. */
	public final BlockStructure blocks;
	public final ArrayList<BiomeStructure> vegetation = new ArrayList<>();
	/** Replacement if the upper height limit can't be met. */
	public ArrayList<Biome> upperReplacements = new ArrayList<Biome>();
	/** Replacement if the lower height limit can't be met. */
	public ArrayList<Biome> lowerReplacements = new ArrayList<Biome>();
	
	/**
	 * Loads the biome from a file.
	 * @param ID
	 * @param file
	 */
	public Biome(String ID, File file) {
		this.ID = ID;
		
		float roughness = 0;
		float hills = 0;
		float mountains = 0;
		float minHeight = 0;
		float maxHeight = 50;
		float chance = 100.0f;
		String type = "ETERNAL_DARKNESS";
		BlockStructure blocks = null;
		
		// Simple parsing:
		boolean startedStructures = false;
		int lineNumber = 0;
		try {
			BufferedReader buf = new BufferedReader(new FileReader(file));
			String line;
			while((line = buf.readLine()) != null) {
				lineNumber++;
				line = line.replaceAll("//.*", ""); // Ignore comments with "//".
				line = line.trim(); // Remove whitespaces before and after the word starts.
				if(line.length() == 0) continue;
				if(startedStructures) {
					String structureID = line.substring(0, line.indexOf(' '));
					BiomeStructure structure = Registries.BIOME_STRUCTURES.getById(structureID);
					if(structure != null) {
						String content = line.substring(ID.length() + 1);
						vegetation.add(structure.readFromString(content));
					} else {
						Log.warning("Could not find structure \"" + line.split("\\s+")[0] + "\" specified in line " + lineNumber + " in file " + file.getPath());
					}
				} else {
					if(line.startsWith("roughness")) {
						roughness = Float.parseFloat(line.substring(9));
					} else if(line.startsWith("hills")) {
						hills = Float.parseFloat(line.substring(5));
					} else if(line.startsWith("mountains")) {
						mountains = Float.parseFloat(line.substring(9));
					} else if(line.startsWith("height")) {
						String[] heightArguments = line.substring(6).split("to");
						minHeight = Float.parseFloat(heightArguments[0].trim());
						maxHeight = Float.parseFloat(heightArguments[1].trim());
					} else if(line.startsWith("type")) {
						type = line.substring(4).trim();
					} else if(line.startsWith("chance")) {
						chance = Float.parseFloat(line.substring(6).trim());
					} else if(line.startsWith("ground_structure")) {
						blocks = new BlockStructure(line.substring(16), lineNumber, file);
					} else if(line.startsWith("structures:")) {
						startedStructures = true;
					} else {
						Log.warning("Could not find argument \"" + line.split("\\s+")[0] + "\" specified in line " + lineNumber + " in file " + file.getPath());
					}
				}
			}
			
			buf.close();
		} catch(Exception e) {
			if(lineNumber != 0) {
				Log.severe("Encountered error while parsing line "+lineNumber+" in file "+file.getPath());
			}
			Log.severe(e);
		}
		Type parsedType = Type.ETERNAL_DARKNESS;
		try {
			parsedType = Type.valueOf(type);
		} catch(Exception e) {
			Log.warning("Incorrect Biome type \""+type+"\" in file "+file.getPath());
		}
		this.type = parsedType;
		this.roughness = roughness;
		this.hills = hills;
		this.mountains = mountains;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.chance = (int)(chance*1000);
		if(blocks == null) {
			blocks = new BlockStructure("cubyz:dirt", 0, file);
			Log.warning("Biome block structure not specified in file "+file.getPath());
		}
		this.blocks = blocks;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public int chance() {
		return chance;
	}
	
	/**
	 * Used to find valid replacement biomes if none was found of the same type.
	 * @param type
	 * @param consumer
	 * @param reg
	 */
	public static void checkLowerTypesInRegistry(Type type, Consumer<Biome> consumer) {
		switch(type) {
			case RAINFOREST:
			case SHRUBLAND:
			case DESERT:
				Registries.BIOMES.byTypeBiomes.get(Type.WARM_OCEAN).forEach(consumer);
				break;
			case SWAMP:
			case FOREST:
			case GRASSLAND:
				Registries.BIOMES.byTypeBiomes.get(Type.OCEAN).forEach(consumer);
				break;
			case TUNDRA:
			case TAIGA:
			case GLACIER:
				Registries.BIOMES.byTypeBiomes.get(Type.ARCTIC_OCEAN).forEach(consumer);
				break;
			case MOUNTAIN_FOREST:
				Registries.BIOMES.byTypeBiomes.get(Type.FOREST).forEach(consumer);
				break;
			case MOUNTAIN_GRASSLAND:
				Registries.BIOMES.byTypeBiomes.get(Type.GRASSLAND).forEach(consumer);
				break;
			case PEAK:
				Registries.BIOMES.byTypeBiomes.get(Type.TUNDRA).forEach(consumer);
				break;
			case WARM_OCEAN:
			case OCEAN:
			case ARCTIC_OCEAN:
				Registries.BIOMES.byTypeBiomes.get(Type.TRENCH).forEach(consumer);
				break;
			default:
				break;
		}
	}

	/**
	 * Used to find valid replacement biomes if none was found of the same type.
	 * @param type
	 * @param consumer
	 * @param reg
	 */
	public static void checkHigherTypesInRegistry(Type type, Consumer<Biome> consumer) {
		switch(type) {
			case SWAMP:
			case RAINFOREST:
			case FOREST:
			case TAIGA:
				Registries.BIOMES.byTypeBiomes.get(Type.MOUNTAIN_FOREST).forEach(consumer);
				break;
			case SHRUBLAND:
			case GRASSLAND:
				Registries.BIOMES.byTypeBiomes.get(Type.MOUNTAIN_GRASSLAND).forEach(consumer);
				break;
			case MOUNTAIN_FOREST:
			case MOUNTAIN_GRASSLAND:
				Registries.BIOMES.byTypeBiomes.get(Type.PEAK).forEach(consumer);
				break;
			case DESERT:
			case TUNDRA:
			case GLACIER:
				Registries.BIOMES.byTypeBiomes.get(Type.PEAK).forEach(consumer);
				break;
			case WARM_OCEAN:
				Registries.BIOMES.byTypeBiomes.get(Type.RAINFOREST).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.SHRUBLAND).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.DESERT).forEach(consumer);
				break;
			case OCEAN:
				Registries.BIOMES.byTypeBiomes.get(Type.SWAMP).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.FOREST).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.GRASSLAND).forEach(consumer);
				break;
			case ARCTIC_OCEAN:
				Registries.BIOMES.byTypeBiomes.get(Type.GLACIER).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.TUNDRA).forEach(consumer);
				break;
				
			case TRENCH:
				Registries.BIOMES.byTypeBiomes.get(Type.ARCTIC_OCEAN).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.OCEAN).forEach(consumer);
				Registries.BIOMES.byTypeBiomes.get(Type.WARM_OCEAN).forEach(consumer);
				break;
			default:
				break;
		}
	}
}
