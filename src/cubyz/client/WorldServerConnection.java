package cubyz.client;

import java.util.UUID;
import java.util.function.Consumer;

import cubyz.world.ChunkVisibilityData;
import cubyz.world.WorldInterface;

public class WorldServerConnection extends WorldInterface {

	@Override
	public void breakBlock(UUID playerUuid, int positionX, int positionY, int positionZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateVisibilityData(UUID playerUuid, int x, int y, int z, int resolution, float priority, Consumer<ChunkVisibilityData> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean authenticate(UUID playerUuid, char[] passphrase, String username) {
		// TODO Auto-generated method stub
		return false;
	}

}
