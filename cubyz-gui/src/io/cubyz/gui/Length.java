package io.cubyz.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.cubyz.utils.log.Log;

public class Length {
	public float data;
	public boolean isPercent = false;
	public Length parent;
	
	public void setAsValue(float data) {
		isPercent 	= false;
		this.data	= data;
	}
	public void setAsPercentage(float data,Length parent) {
		this.isPercent 	= true;
		this.data 		= data;
		this.parent 	= parent;
	}
	public float getAsValue() {
		return isPercent?data*parent.getAsValue():data;
	}
	public float getAsPercent() {
		return isPercent?data:data/parent.getAsValue();
	}
	
	public JsonElement toJson() {
		if(isPercent) {
			return new JsonPrimitive(""+(data*100)+"%");
		}else return new JsonPrimitive(data);
	}
	public void fromJson(JsonElement object,Length parent) {
		if(!object.isJsonPrimitive())
			return;
		JsonPrimitive jp = (JsonPrimitive)object;
		if(jp.isNumber()) {
			data = jp.getAsInt();
			isPercent = false;
			return;
		}
		if(jp.isString()) {
			String content = jp.getAsString();
			if(!content.isEmpty()&&content.contains("%")) {
				data = Float.parseFloat(content.replaceAll("%", " "))/100;
				isPercent = true;
				this.parent = parent;
				return;
			}
		}
		Log.severe("JSON unknown property");
		Log.severe(object);
	}
	public void fromJsonAttribute(JsonObject object ,String attributeName,Length parent) {
		fromJson(object.getAsJsonPrimitive(attributeName),parent);
	}
	public void changeParent(Length length) {
		this.parent = length;
	}
	
	public String toString() {
		return "Value: "+getAsValue();
	}
}
