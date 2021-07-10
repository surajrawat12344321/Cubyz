package cubyz.server.rotationmodes;

import java.util.HashMap;

import cubyz.world.blocks.Model;
import cubyz.world.blocks.RotationMode;

public class NoRotation implements RotationMode {
	private static HashMap<String, NoRotation> loadedModels = new HashMap<>();
	
	Model model;

	@Override
	public String getID() {
		return "cubyz:no_rotation";
	}

	@Override
	public RotationMode createModel(String modelID) {
		// Check if it was already loaded:
		NoRotation mode = loadedModels.get(modelID);
		if(mode != null) return mode;
		// Otherwise create it:
		mode = new NoRotation();
		mode.model = Model.loadModelFromID(modelID);
		return mode;
	}

	@Override
	public Model getModel(int blockData) {
		return model;
	}

}
