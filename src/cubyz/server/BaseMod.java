package cubyz.server;

import cubyz.server.modding.Mod;
import cubyz.server.rotationmodes.NoRotation;
import cubyz.world.Registries;
import cubyz.world.blocks.Blocks;

public class BaseMod extends Mod {
	
	@Override
	public void init() {
		Registries.BLOCK_REGISTRIES.add(new Blocks());
		Registries.ROTATION_MODES.add(new NoRotation());
	}

	@Override
	public String getName() {
		return "cubyz:base";
	}

}
