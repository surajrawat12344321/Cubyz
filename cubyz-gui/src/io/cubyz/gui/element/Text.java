package io.cubyz.gui.element;


import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.sql.Time;
import java.util.Arrays;
import java.util.Iterator;

import org.joml.Vector2d;
import org.lwjgl.system.MemoryUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Scene;
import io.cubyz.rendering.GraphicFont;
import io.cubyz.rendering.GraphicFont.Glyph;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Keys;
import io.cubyz.rendering.Shader;
import io.cubyz.rendering.Texture;

import static org.lwjgl.glfw.GLFW.*;

public class Text extends Component {
	//statics
	static GraphicFont font = new GraphicFont();
	
	static{
		font.loadFromAwt();
	}
	
	
	static int vbo = -1;
	static Shader shader = new Shader();
	
	//state of the button
	public boolean pressed;
	public boolean hovered;
		
	//action
	public Runnable onAction;
	
	//colors
	public float[] color_std 	 = 	{ 156, 166, 191}; // standart colour
	public float[] color_pressed = 	{ 146, 154, 179}; // pressed colour
	public float[] color_hovered = 	{ 156, 166, 221}; // hovered colour
	
	//Text
	public String text = new String("");
	
	static void initOpenGLStuff() {
		if (vbo != -1)
			return;
		// vertex buffer
		float rawdata[] = { 
				0,0,		0,0,
				0,-1,		0,1,
				1,0,		1,0,
				1,-1,		1,1
			};
		FloatBuffer buffer = MemoryUtil.memAllocFloat(rawdata.length);
		buffer.put(rawdata).flip();
		
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,4*4,0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,4*4,8);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		//Shader
		shader.loadFromFile("assets/cubyz/shaders/Gui/GuiText.vs", "assets/cubyz/shaders/Gui/GuiText.fs");
		
	}

	@Override
	public String getID() {
		return "cubyz:text";
	}
	
	public void setOnAction(Runnable onAction) {
		this.onAction = onAction;
	}

	@Override
	public void create(JsonObject object) {
		super.create(object);
		initOpenGLStuff();
		
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
			text = object.get("text").getAsString();
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

	public void update(Scene scene) {
		Vector2d mousepos = Input.getMousePosition(scene);
		
		hovered = (left<=mousepos.x&&
			top<=mousepos.y&&
			left+width>=mousepos.x&&
			top+height>=mousepos.y);

		boolean old_pressed = pressed;
		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
		if(!pressed&&old_pressed&&onAction!=null)
			onAction.run();
	}
	@Override
	public void draw(Scene scene) {
		update(scene);
		shader.bind();
		font.getTexture().bind();
		
		
		int loc_texCoords = shader.getUniformLocation("texture_rect");
		int loc_rect = shader.getUniformLocation("rect");
		
		int height = font.getTexture().height;
		int width = font.getTexture().width;
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		float offset = 0;
		for (int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);
			
			Glyph glyph = font.getGlyph(letter);
			
			//System.out.println("Left:"+(((float)glyph.texture_left)/width));
			//System.out.println("Top:"+(((float)glyph.texture_top)/height));
			//System.out.println("Width:"+(((float)glyph.texture_width)/width));
			//System.out.println("Height:"+(((float)glyph.texture_height)/height));

			
			float tex_left = ((float)glyph.texture_left)/width;
			float tex_top =	((float)glyph.texture_top)/height;
			float tex_width = ((float)glyph.texture_width)/width; 
			float tex_height = ((float)glyph.texture_height)/height;
			
			
			float scalex = 0.5f;
			float scaley = 0.5f;
			
			
			glUniform4f(loc_texCoords, tex_left,tex_top,tex_width,tex_height);
			glUniform4f(loc_rect, offset*scalex,0f,tex_width*scalex,tex_height*scalex);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			
			offset+=tex_width;
		}
		
		font.getTexture().unbind();
		shader.unbind();
	}
}
