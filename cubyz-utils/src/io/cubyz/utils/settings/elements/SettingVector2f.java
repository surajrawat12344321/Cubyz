package io.cubyz.utils.settings.elements;


import com.google.gson.JsonObject;

import io.cubyz.utils.settings.Setting;

/**
 * 
 * Setting that saves 2 floats.
 *
 */
public class SettingVector2f extends Setting{
	public float x;
	public float y;

	public SettingVector2f() {
		this.x = 0;
		this.y = 0;
	}
	public SettingVector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String getID() {
		return "vector2f";
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		object.addProperty("x", x);
		object.addProperty("y", y);
		return object;
	}

	@Override
	public void fromJson(JsonObject object) {
		x = object.getAsJsonPrimitive("x").getAsFloat();
		y = object.getAsJsonPrimitive("y").getAsFloat();
	}
	
}
