package io.cubyz.gui;

import java.util.ArrayList;

import com.google.gson.*;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * 	IMPORTANT: DO NOT USE MULTIPLE PARAMETER IN THE CONSTRUCTOR OF YOUR SUBCLASS
 */
public abstract class Component implements RegistryElement{
	ArrayList<Component> children = new ArrayList<Component>();
	
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
	public void create(JsonObject object,Component parent) {
		this.parent = parent;
		
		// Default to center of parent if nothing more is specified:
		if(parent != null) {
			left.setAsPercentage(0.5f, parent.width);
			top.setAsPercentage(0.5f, parent.height);
		}
		
		if(object.has("left"))
			this.left.fromJsonAttribute(object,"left",parent.width);
		if(object.has("top"))
			this.top.fromJsonAttribute(object,"top",parent.height);
		if(object.has("width"))
			this.width.fromJsonAttribute(object,"width",parent.width);
		if(object.has("height"))
			this.height.fromJsonAttribute(object,"height",parent.height);
		if(object.has("name"))
			this.name = object.getAsJsonPrimitive("name").getAsString();
		if(object.has("origin")) {
			JsonArray Jorigin = object.getAsJsonArray("origin");
			this.originLeft.fromJson(Jorigin.get(0),width);
			this.originTop.fromJson(Jorigin.get(1),height);
		}

		JsonArray jchildren = object.getAsJsonArray("children");
		if(jchildren != null) {
			for (JsonElement jsonElement : jchildren) {
				JsonObject jsonObject = (JsonObject)jsonElement;
				children.add(ComponentRegistry.createByJson(jsonObject,this));
			}
		}
	}
	public  JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", getID());
		obj.add("left", left.toJson());
		obj.add("top", top.toJson());
		obj.add("width", width.toJson());
		obj.add("height", height.toJson());
		obj.addProperty("name", name);
		
		JsonArray origin = new JsonArray();
		origin.add(originLeft.toJson());
		origin.add(originTop.toJson());
		obj.add("origin", origin);
		
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
		for (Component component : children) {
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
