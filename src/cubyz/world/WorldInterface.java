package cubyz.world;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * 
 * This is the connection interface between the User/RenderUniverse and the actual Universe.
 *
 */

public abstract class WorldInterface {
	public abstract void breakBlock(UUID playerUuid,int positionX,int positionY,int positionZ);
	public abstract void generateVisibilityData(UUID playerUuid, int x, int y, int z, int resolution, float priority, Consumer<ChunkVisibilityData> callback);
}
