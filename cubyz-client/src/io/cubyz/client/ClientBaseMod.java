package io.cubyz.client;

import io.cubyz.server.modding.Mod;
import io.cubyz.world.Registries;
import io.meshes.BlockMeshes;

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
