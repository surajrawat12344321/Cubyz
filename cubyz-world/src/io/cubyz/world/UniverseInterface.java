package io.cubyz.world;

import java.util.UUID;

/**
 * 
 * This is the connection interface between the User/RenderUniverse and the actual Universe.
 *
 */

public abstract class UniverseInterface {
	public abstract void breakBlock(UUID playerUuid,int positionX,int positionY,int positionZ);
}
