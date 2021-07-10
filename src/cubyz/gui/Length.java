package cubyz.gui;

import cubyz.utils.json.*;
import cubyz.utils.log.Log;

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
			return new JsonString(""+(data*100)+"%");
		}else return new JsonFloat(data);
	}
	public void fromJson(JsonElement object,Length parent) {
		if(object instanceof JsonString) {
			String content = object.getString("50%");
			if(!content.isEmpty()&&content.contains("%")) {
				data = Float.parseFloat(content.replaceAll("%", " "))/100;
				isPercent = true;
				this.parent = parent;
				return;
			}
		} else {
			data = object.getFloat(0);
			isPercent = false;
			return;
		}
		Log.severe("JSON unknown property");
		Log.severe(object);
	}

	public void changeParent(Length length) {
		this.parent = length;
	}
	
	public String toString() {
		return "Value: "+getAsValue();
	}
}
