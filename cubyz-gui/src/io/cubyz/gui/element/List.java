package io.cubyz.gui.element;

import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.gui.Length;
import io.cubyz.utils.log.Log;

/*
 * Invisible Frame,
 * All of the 
 * 
 * */

public class List extends Component {
	public enum Direction{
		horizontal,vertical;
	}
	
	public Direction direction = Direction.vertical;
	public Length spacing = new Length();
	
	public float sumSize = 0; //The sum of all the childrens sizes + spacings
	
	@Override
	public String getID() {
		return "cubyz:list";
	}
	@Override
	public void create(JsonObject object, Component parent) {
		super.create(object, parent);
		if(object.has("spacing")) 
			this.spacing.setAsValue(object.get("spacing").getAsFloat());
		if(object.has("direction")) {
			String dir = object.get("direction").getAsString();
			if(dir.equals("vertical")) {
				direction = Direction.vertical;
			}else if(dir.equals("horizontal")) {
				direction = Direction.horizontal;
			}else {
				Log.warning("Unkown direction for list:"+dir);
			}
		}		
		
	}
	public void draw(Design design,float parentialOffsetX,float parentialOffsetY) {
		float initialParentialOffsetX = parentialOffsetX;
		float initialParentialOffsetY = parentialOffsetY;
		
		for (Component component : children) {

			if(direction == Direction.horizontal) {
				component.draw(design,
						parentialOffsetX+left.getAsValue()/*-component.originLeft.getAsValue()*/,
						parentialOffsetY+top.getAsValue()-component.originTop.getAsValue());	
				parentialOffsetX+=spacing.getAsValue()+component.left.getAsValue()+component.width.getAsValue();
			}else {
				component.draw(design,
						parentialOffsetX+left.getAsValue()-component.originLeft.getAsValue(),
						parentialOffsetY+top.getAsValue()/*-component.originTop.getAsValue()*/);
				parentialOffsetY+=spacing.getAsValue()+component.top.getAsValue()+component.height.getAsValue();
			}
		}
		if(direction == Direction.horizontal) {
			sumSize = parentialOffsetX-initialParentialOffsetX;
		}else {
			sumSize = parentialOffsetY-initialParentialOffsetY;
		}
	}
	/**
		When overriding: Make sure to call super.update(design); at the <b>START</b> of the function.		
		@param design
	 */
	public void update(Design design,float parentialOffsetX,float parentialOffsetY) {
		
		if(direction == Direction.horizontal) {
			parentialOffsetX+=sumSize;
		}else {
			parentialOffsetY+=sumSize;
		}
		for (int i = children.size()-1; i >= 0; i--) {
			Component component = children.get(i);
		
			if(direction == Direction.horizontal) {
				parentialOffsetX-=spacing.getAsValue()+component.left.getAsValue()+component.width.getAsValue();
				component.update(design,
						parentialOffsetX+left.getAsValue()/*-component.originLeft.getAsValue()*/,
						parentialOffsetY+top.getAsValue()-component.originTop.getAsValue());	
			}else {
				parentialOffsetY-=spacing.getAsValue()+component.top.getAsValue()+component.height.getAsValue();
				component.update(design,
						parentialOffsetX+left.getAsValue()-component.originLeft.getAsValue(),
						parentialOffsetY+top.getAsValue()/*-component.originTop.getAsValue()*/);
			}
		}
	}
}
