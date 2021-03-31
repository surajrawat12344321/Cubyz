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
	
	@Override
	public abstract String getID();
	/**
	 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
	 */
	public Component() {}
	public void create(JsonObject object){
		this.left = object.getAsJsonPrimitive("left").getAsFloat();
		this.top = object.getAsJsonPrimitive("top").getAsFloat();
		this.width = object.getAsJsonPrimitive("width").getAsFloat();
		this.height = object.getAsJsonPrimitive("height").getAsFloat();
	}
	public  JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", getID());
		obj.addProperty("left", left);
		obj.addProperty("top", top);
		obj.addProperty("width", width);
		obj.addProperty("height", height);
		return obj;
	}
	public abstract void draw(Scene scene);
	
}
