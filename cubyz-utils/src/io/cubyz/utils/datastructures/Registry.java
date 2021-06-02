package io.cubyz.utils.datastructures;

import java.util.ArrayList;
import java.util.HashMap;

import io.cubyz.utils.log.Log;

public class Registry<T extends RegistryElement> {
	HashMap<String, T> elements = new HashMap<String, T>();
	ArrayList<T> array = new ArrayList<T>();
	
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
		array.add(element);
	}
	public T getById(String string) {
		return elements.get(string);
	}
	
	public void clear() {
		elements.clear();
	}
	
	public T[] toArray(T[] array) {
		return this.array.toArray(array);
	}
}
