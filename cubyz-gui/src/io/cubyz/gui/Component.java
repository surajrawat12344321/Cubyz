package io.cubyz.gui;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
 */
public abstract class Component implements RegistryElement{
	ArrayList<Component> children = new ArrayList<Component>();
	
	public float left,top,width,height;
	public String name = new String();
	
	@Override
	public abstract String getID();
	/**
	 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
	 */
	public Component() {}
	public void create(JsonObject object){
		if(object.has("left"))
			this.left = object.getAsJsonPrimitive("left").getAsFloat();
		if(object.has("top"))
			this.top = object.getAsJsonPrimitive("top").getAsFloat();
		if(object.has("width"))
			this.width = object.getAsJsonPrimitive("width").getAsFloat();
		if(object.has("height"))
			this.height = object.getAsJsonPrimitive("height").getAsFloat();
		if(object.has("name"))
			this.name = object.getAsJsonPrimitive("name").getAsString();
	}
	public  JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", getID());
		obj.addProperty("left", left);
		obj.addProperty("top", top);
		obj.addProperty("width", width);
		obj.addProperty("height", height);
		obj.addProperty("name", name);
		return obj;
	}
	public abstract void draw(Scene scene);
	
}
