package io.cubyz.utils.datastructures;

import java.util.HashMap;

import io.cubyz.utils.log.Log;

public class Registry<T extends RegistryElement> {
	HashMap<String, T> elements = new HashMap<String, T>();
	
	@SuppressWarnings("unchecked")
	public Registry(T ...array){
		for (T registryElement : array) {
			add(registryElement);
		}
	}
	
	
	public void add(T element) {
		if(elements.containsKey(element.getID()))
			Log.severe(new Exception("registryID duplicant."));
		elements.put(element.getID(), element);
	}
	public T getById(String string) {
		return elements.get(string);
	}
	
	public void clear() {
		elements.clear();
	}
}
