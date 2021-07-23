package cubyz.world.terrain;

import java.util.HashMap;

import cubyz.utils.datastructures.Registry;
import cubyz.utils.datastructures.random.RandomList;

/**
 * A registry for biomes that also maps the biomes by their type.
 * @author mint
 *
 */
public class BiomeRegistry extends Registry<Biome> {
	public final HashMap<Biome.Type, RandomList<Biome>> byTypeBiomes = new HashMap<Biome.Type, RandomList<Biome>>();
	public BiomeRegistry() {
		for(Biome.Type type : Biome.Type.values()) {
			byTypeBiomes.put(type, new RandomList<Biome>(new Biome[16]));
		}
	}
	
	@Override
	public boolean add(Biome biome) {
		if(super.add(biome)) {
			byTypeBiomes.get(biome.type).add(biome);
			return true;
		}
		return false;
	}
}
