package cubyz.utils.datastructures;

import cubyz.utils.json.JsonObject;

public interface DataOrientedRegistry extends RegistryElement {
	void clear();
	void register(String registryID, JsonObject json);
}
