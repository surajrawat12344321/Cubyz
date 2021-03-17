package io.cubyz.base;


import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.Registry;
import io.cubyz.blocks.RotationMode;
import io.cubyz.blocks.rotation.*;
import io.cubyz.entity.EntityModel;
import io.cubyz.entity.models.Quadruped;

/**
 * Registers objects that are only available on the client.
 */

public class ServerProxy extends CommonProxy {

	public void init() {
		super.init();
	}
	
	public void preInit() {
		super.preInit();
		registerRotationModes(CubyzRegistries.ROTATION_MODE_REGISTRY);
		registerEntityModels(CubyzRegistries.ENTITY_MODEL_REGISTRY);
	}

	private void registerRotationModes(Registry<RotationMode> reg) {
		reg.register(new NoRotation());
		reg.register(new TorchRotation());
		reg.register(new LogRotation());
		reg.register(new StackableRotation());
		reg.register(new FenceRotation());
	}

	private void registerEntityModels(Registry<EntityModel> reg) {
		reg.register(new Quadruped());
	}
}
