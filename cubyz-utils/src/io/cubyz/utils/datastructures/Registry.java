package io.cubyz.utils.datastructures;

import java.util.HashMap;

import io.cubyz.utils.log.Log;

public class Registry {
	HashMap<String, RegistryElement> elements = new HashMap<String, RegistryElement>();
	
	public Registry(RegistryElement...array){
		for (RegistryElement registryElement : array) {
			add(registryElement);
		}
	}
	
	
	public void add(RegistryElement element) {
		if(elements.containsKey(element.getID()))
			Log.severe(new Exception("registryID duplicant."));
		elements.put(element.getID(), element);
	}
	public RegistryElement getById(String string) {
		return elements.get(string);
	}
}
