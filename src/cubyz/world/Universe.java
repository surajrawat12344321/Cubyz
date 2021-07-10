package cubyz.world;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Handles all Worlds of this Universe.
 * TODO
 */

public class Universe extends UniverseInterface {
	

	@Override
	public void breakBlock(UUID playerUuid, int positionX, int positionY, int positionZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateVisibilityData(int x, int y, int z, int resolution, float priority, Consumer<ChunkVisibilityData> callback) {
				
	}

}
