package io.cubyz.utils.settings;

import com.google.gson.JsonObject;

import io.cubyz.utils.datastructures.RegistryElement;
import io.cubyz.utils.log.Log;

public abstract class Setting implements RegistryElement {
	public Setting() {}
	
	//json
	public abstract void fromJson(JsonObject object);
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("type", getID());
		return object;
	}
	
	//displayed properties
	public String name;
	public String description;
	
	//Check for type
	public void checkUp(String ExpectedType) {
		if(!getID().equals(ExpectedType)) {
			Log.severe("Setting type are wrong.Expected:"+ExpectedType+", but got: "+getID());
			Log.severe(this);			
		}
	}
}
