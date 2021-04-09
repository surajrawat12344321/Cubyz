package io.cubyz.gui.element;

import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.joml.Vector2d;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.gui.rendering.CubyzGraphics2D;
import io.cubyz.gui.rendering.GraphicFont;
import io.cubyz.gui.rendering.Input;
import io.cubyz.gui.rendering.Keys;
import io.cubyz.gui.rendering.Shader;

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
	
	//colors
	public float[] color_std 	 = 	{ 156, 166, 191}; // standart colour
	public float[] color_pressed = 	{ 146, 154, 179}; // pressed colour
	public float[] color_hovered = 	{ 156, 166, 221}; // hovered colour
	
	//Text
	private String text = new String("a");
	private TextLayout layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
	public TextHitInfo cursorPosition = null;
	public float cursorX = 0;
	public boolean editable = true;
	
	
	@Override
	public String getID() {
		return "cubyz:text";
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
		updateText();
	}
	void setText(String string) {
		this.text = string;
		updateText();
	}
	
	public void addTextAtCursor(String string) {
		if(cursorPosition != null) {
			int oldCursorIndex = cursorPosition.getCharIndex() + string.length();
			if(cursorPosition.isLeadingEdge())
				this.text = text.substring(0, oldCursorIndex-1)+string+text.substring(oldCursorIndex-1);
			else
				this.text = text.substring(0, oldCursorIndex)+string+text.substring(oldCursorIndex);
			updateText();
			if(cursorPosition.isLeadingEdge())
				cursorPosition = TextHitInfo.leading(oldCursorIndex);
			else
				cursorPosition = TextHitInfo.trailing(oldCursorIndex);
			
			moveCursor(0);
		}
	}
	
	/**
	 * Removes the selected text or if no text is selected, removes the right or left character depending on what key is pressed.
	 * @param isRightDelete on which side the character should be removed.
	 */
	public void deleteTextAtCursor(boolean isRightDelete) {
		if(cursorPosition != null) {
			boolean isLeading = cursorPosition.isLeadingEdge();
			int[] selection;
			// Make a selection to determine which character should be removed:
			if(true) { // If nothing is selected.
				TextHitInfo oldPosition = cursorPosition;
				if(isRightDelete) {
					cursorPosition = layout.getNextRightHit(cursorPosition);
				} else {
					cursorPosition = layout.getNextLeftHit(cursorPosition);
				}
				selection = layout.getLogicalRangesForVisualSelection(oldPosition, cursorPosition);
			} else {
				// TODO
			}
			int oldPositionIndex = cursorPosition.getCharIndex();
			// Remove all selected characters:
			for(int i = 0; i < selection.length; i += 2) {
				int start = selection[i];
				int end = selection[i+1];
				deleteTextRange(start, end);
				// Go through other indices and shift them:
				for(int j = i + 2; j < selection.length; j += 2) {
					if(selection[j] >= end) {
						selection[j] -= end - start;
						selection[j+1] -= end - start;
					}
				}
				// Also move the current cursor location:
				if(oldPositionIndex >= end) {
					oldPositionIndex -= end - start;
				}
			}
			updateText();
			// Update cursor:
			if(isLeading)
				cursorPosition = TextHitInfo.leading(oldPositionIndex);
			else
				cursorPosition = TextHitInfo.trailing(oldPositionIndex);
			
			moveCursor(0);
		}
	}
	private void deleteTextRange(int start, int end) {
		text = text.substring(0, start) + text.substring(end);
	}
	private void updateText() {
		layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
		width.setAsValue((float)layout.getBounds().getWidth()*height.getAsValue()/font.font.getSize());
	}
	public void moveCursor(int offset) {
		if(offset < 0) {
			while(offset++ < 0) {
				cursorPosition = layout.getNextLeftHit(cursorPosition);
			}
		} else if(offset > 0) {
			while(offset-- > 0) {
				cursorPosition = layout.getNextRightHit(cursorPosition);
			}
		}
		//cursorPosition = cursorPosition.getOffsetHit(position);
		Point2D.Float cursorPos = new Point2D.Float();
		layout.hitToPoint(cursorPosition, cursorPos);
		cursorX = cursorPos.x*(float)height.getAsValue()/font.font.getSize();
	}
	String getText() {
		return text;
	}
	
	public void update(Design design,float parentalOffsetX,float parentalOffsetY) {
		Vector2d mousepos = Input.getMousePosition(design);
		mousepos.x-= parentalOffsetX + left.getAsValue();
		mousepos.y-= parentalOffsetY + top.getAsValue();
		
		hovered = (0<=mousepos.x&&
			0<=mousepos.y&&
			width.getAsValue()>=mousepos.x&&
			height.getAsValue()>=mousepos.y);
		
		boolean old_pressed = pressed;
		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
		if(pressed)
			Input.selectedText = this;

		if(!pressed && old_pressed) {
			float ratio = (float)height.getAsValue()/font.font.getSize();
			TextHitInfo info = layout.hitTestChar((float)mousepos.x/ratio, (float)mousepos.y/ratio);
			cursorPosition = info;
			moveCursor(0);
		}
	}
	
	
	@Override
	public void draw(Design design,float parentalOffsetX,float parentalOffsetY) {
		update(design,parentalOffsetX,parentalOffsetY);
		CubyzGraphics2D.instance.textHeight = height.getAsValue();
		
		
		
		// Undo the ratio multiplication that is done later on the gpu:
		float ratio = (float)height.getAsValue()/font.font.getSize();
		layout.draw(CubyzGraphics2D.instance, (parentalOffsetX+left.getAsValue())/ratio, (parentalOffsetY+top.getAsValue()+height.getAsValue())/ratio);
		
		
		
		if(cursorPosition != null) {
			CubyzGraphics2D.instance.drawLine(left.getAsValue() + parentalOffsetX + cursorX, top.getAsValue() + parentalOffsetY, 0, height.getAsValue());

		}

		super.draw(design,parentalOffsetX,parentalOffsetY);
	}
}
