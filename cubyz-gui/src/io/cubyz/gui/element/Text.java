package io.cubyz.gui.element;


import java.awt.font.TextLayout;
import java.util.Arrays;

import org.joml.Vector2d;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.rendering.CubyzGraphics2D;
import io.cubyz.rendering.GraphicFont;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Keys;
import io.cubyz.rendering.Shader;

public class Text extends Component {
	//statics
	static GraphicFont font = new GraphicFont();
	
	static{
		font.loadFromAwt();
		CubyzGraphics2D.instance.font = font;
	}
	
	
	static int vbo = -1;
	static Shader shader = new Shader();
	
	//state of the Text
	public boolean pressed;
	public boolean hovered;
		
	//action
	public Runnable onAction;
	
	//colors
	public float[] color_std 	 = 	{ 156, 166, 191}; // standart colour
	public float[] color_pressed = 	{ 146, 154, 179}; // pressed colour
	public float[] color_hovered = 	{ 156, 166, 221}; // hovered colour
	
	//Text
	private String text = new String("a");
	private TextLayout layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
	public int cursorPosition = -1;
	public boolean editable = true;
	
	@Override
	public String getID() {
		return "cubyz:text";
	}
	
	public void setOnAction(Runnable onAction) {
		this.onAction = onAction;
	}

	@Override
	public void create(JsonObject object, Component parent) {
		super.create(object, parent);
		
		if(object.has("color")) {
			color_std[0] = object.get("color").getAsJsonArray().get(0).getAsFloat();
			color_std[1] = object.get("color").getAsJsonArray().get(1).getAsFloat();
			color_std[2] = object.get("color").getAsJsonArray().get(2).getAsFloat();	
		}
		if(object.has("colorHovered")) {
			color_hovered[0] = object.get("colorHovered").getAsJsonArray().get(0).getAsFloat();
			color_hovered[1] = object.get("colorHovered").getAsJsonArray().get(1).getAsFloat();
			color_hovered[2] = object.get("colorHovered").getAsJsonArray().get(2).getAsFloat();	
		}
		if(object.has("colorPressed")) {
			color_pressed[0] = object.get("colorPressed").getAsJsonArray().get(0).getAsFloat();
			color_pressed[1] = object.get("colorPressed").getAsJsonArray().get(1).getAsFloat();
			color_pressed[2] = object.get("colorPressed").getAsJsonArray().get(2).getAsFloat();	
		}
		if(object.has("text")) {
			setText(object.get("text").getAsString());
		}
		
	}
	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		
		if(!Arrays.equals(color_std,new float[]{156, 166, 191})) {
			
			JsonArray color = new JsonArray();
			color.add(color_std[0]);
			color.add(color_std[1]);
			color.add(color_std[2]);
			
			object.add("color", color);
		}
		if(!Arrays.equals(color_hovered,new float[]{156, 166, 221})) {
			JsonArray color = new JsonArray();
			color.add(color_hovered[0]);
			color.add(color_hovered[1]);
			color.add(color_hovered[2]);
			
			object.add("colorHovered", color);
		}
		if(!Arrays.equals(color_pressed,new float[]{146, 154, 179})) {
			JsonArray color = new JsonArray();
			color.add(color_pressed[0]);
			color.add(color_pressed[1]);
			color.add(color_pressed[2]);
			
			object.add("colorPressed", color);
		}
		if(!text.equals("")) {
			object.addProperty("shadow", text);
		}
		return object;
	}

	void addText(String string) {
		this.text += string;
		layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
	}
	void setText(String string) {
		this.text = string;
		layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
	}
	public void addTextAtCursor(String string) {
		if(cursorPosition<0||cursorPosition>text.length())
			return;
		this.text = text.substring(0, cursorPosition)+string+text.substring(cursorPosition);
		layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());	
		cursorPosition+=string.length();
	}
	public void deleteTextAtCursor() {
		if(cursorPosition<=0||cursorPosition>text.length())
			return;
		this.text = text.substring(0, cursorPosition-1)+text.substring(cursorPosition);
		layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());	
		cursorPosition--;
	}
	public void moveCursor(int position) {
		cursorPosition += position;
		if(cursorPosition>=text.length())
			cursorPosition = text.length()-1;
		if(cursorPosition<0)
			cursorPosition = 0;
	}
	String getText() {
		return text;
	}
	
	public void update(Design design,float parentalOffsetX,float parentalOffsetY) {
		Vector2d mousepos = Input.getMousePosition(design);
		mousepos.x-= parentalOffsetX-originLeft.getAsValue();
		mousepos.y-= parentalOffsetY-originTop.getAsValue();
		
		hovered = (left.getAsValue()<=mousepos.x&&
			top.getAsValue()<=mousepos.y&&
			left.getAsValue()+width.getAsValue()>=mousepos.x&&
			top.getAsValue()+height.getAsValue()>=mousepos.y);

		boolean old_pressed = pressed;
		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
		if(pressed)
			Input.selectedText = this;
		//if(name.equals("unicode"))
		//	System.out.println(width.getAsValue());
		
		if(!pressed&&old_pressed&&onAction!=null)
			onAction.run();	
	}
	
	
	@Override
	public void draw(Design design,float parentalOffsetX,float parentalOffsetY) {
		update(design,parentalOffsetX,parentalOffsetY);
		CubyzGraphics2D.instance.textHeight = height.getAsValue();
		
		
		//cursor clicked
		if(pressed)
			CubyzGraphics2D.instance.cursor_clickedPosition = (float)Input.getMousePosition(design).x;
		else
			CubyzGraphics2D.instance.cursor_clickedPosition = -1;
		
		
		
		CubyzGraphics2D.instance.cursor_position = this.cursorPosition;
		// Undo the ratio multiplication that is done later on the gpu:
		float ratio = (float)height.getAsValue()/font.getTexture().height;
		layout.draw(CubyzGraphics2D.instance, (parentalOffsetX+left.getAsValue()-originLeft.getAsValue())/ratio, (parentalOffsetY+top.getAsValue()+height.getAsValue()-originTop.getAsValue())/ratio);
		this.cursorPosition = CubyzGraphics2D.instance.cursor_position;
		
		super.draw(design,parentalOffsetX,parentalOffsetY);
		width.setAsValue(CubyzGraphics2D.instance.textWidth);
	}
}
