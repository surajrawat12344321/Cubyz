package io.cubyz.gui;

import java.util.ArrayList;

import io.cubyz.utils.datastructures.RegistryElement;
import io.cubyz.utils.json.*;

/**
 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
 */
public abstract class Component implements RegistryElement{
	public ArrayList<Component> children = new ArrayList<Component>();
	
	public Length 	left 		= new Length(),	top 		= new Length(),	
					width		= new Length(),	height		= new Length(),
					originLeft 	= new Length(), originTop	= new Length();
	public String name 			= new String();
	protected Scene scene;
	protected Component parent 	= null;
	
	@Override
	public abstract String getID();
	/**
	 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
	 */
	public Component() {}
	public void create(JsonObject object, Component parent) {
		this.parent = parent;
		
		// Default to center of parent if nothing more is specified:
		if(parent != null) {
			left.setAsPercentage(0.5f, parent.width);
			top.setAsPercentage(0.5f, parent.height);
		}
		
		if(object.has("left"))
			this.left.fromJson(object.get("left"), parent.width);
		if(object.has("top"))
			this.top.fromJson(object.get("top"), parent.height);
		if(object.has("width"))
			this.width.fromJson(object.get("width"), parent.width);
		if(object.has("height"))
			this.height.fromJson(object.get("height"), parent.height);
		this.name = object.getString("name", "");
		ArrayList<JsonElement> jsonOrigin = object.getArrayNoNull("origin").array;
		if(jsonOrigin.size() >= 2) {
			this.originLeft.fromJson(jsonOrigin.get(0), width);
			this.originTop.fromJson(jsonOrigin.get(1), height);
		}

		JsonArray jchildren = object.getArrayNoNull("children");
		for (JsonElement jsonElement : jchildren.array) {
			JsonObject jsonObject = (JsonObject)jsonElement;
			children.add(ComponentRegistry.createByJson(jsonObject, this));
		}
	}
	public  JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.put("type", getID());
		obj.put("left", left.toJson());
		obj.put("top", top.toJson());
		obj.put("width", width.toJson());
		obj.put("height", height.toJson());
		obj.put("name", name);
		
		JsonArray origin = new JsonArray();
		origin.add(originLeft.toJson());
		origin.add(originTop.toJson());
		obj.put("origin", origin);
		
		return obj;
	}

	/**
		When overriding: Make sure to call super.draw(design); at the <b>END</b> of the function.
		@param design
	*/
	public void draw(Design design,float parentialOffsetX,float parentialOffsetY) {
		for (Component component : children) {
			component.draw(design,
					parentialOffsetX+left.getAsValue()-component.originLeft.getAsValue(),
					parentialOffsetY+top.getAsValue()-component.originTop.getAsValue());
		}
	}
	/**
		When overriding: Make sure to call super.update(design); at the <b>START</b> of the function.		
		@param design
	 */
	public void update(Design design,float parentialOffsetX,float parentialOffsetY) {
		for (int i = children.size()-1; i >= 0; i--) {
			Component component = children.get(i);
			component.update(design,
					parentialOffsetX+left.getAsValue()-component.originLeft.getAsValue(),
					parentialOffsetY+top.getAsValue()-component.originTop.getAsValue());
		}
	}
	public void setScene(Scene scene) {
		this.scene = scene;
		for(Component child : children) {
			child.setScene(scene);
		}
	}

	public void add(Component child) {
		children.add(child);
		child.setParent(this);
	}
	public void remove(Component child) {
		children.remove(child);
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
}
