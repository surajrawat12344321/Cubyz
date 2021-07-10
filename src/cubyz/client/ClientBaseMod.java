package cubyz.client;

import cubyz.client.meshes.BlockMeshes;
import cubyz.server.modding.Mod;
import cubyz.world.Registries;

public class ClientBaseMod extends Mod {
	
	@Override
	public void init() {
		Registries.BLOCK_REGISTRIES.add(new BlockMeshes());
	}

	@Override
	public String getName() {
		return "cubyz:client_base";
	}
	
}
