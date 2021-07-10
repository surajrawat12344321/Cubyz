package cubyz.gui.element;

import cubyz.gui.Component;
import cubyz.gui.Design;
import cubyz.gui.Length;
import cubyz.utils.json.*;
import cubyz.utils.log.Log;

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
		this.spacing.setAsValue(object.getFloat("spacing", 0));
		String dir = object.getString("direction", "vertical");
		if(dir.equals("vertical")) {
			direction = Direction.vertical;
		} else if(dir.equals("horizontal")) {
			direction = Direction.horizontal;
		} else {
			Log.warning("Unkown direction for list:"+dir);
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
