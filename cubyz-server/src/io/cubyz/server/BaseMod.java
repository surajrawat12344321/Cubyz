package io.cubyz.server;

import io.cubyz.server.modding.Mod;
import io.cubyz.world.Registries;
import io.cubyz.world.blocks.Blocks;

public class BaseMod extends Mod {
	
	@Override
	public void init() {
		Registries.BLOCK_REGISTRIES.add(new Blocks());
	}

	@Override
	public String getName() {
		return "cubyz:base";
	}

}
