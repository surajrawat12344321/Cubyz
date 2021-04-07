package io.cubyz.gui.element;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.joml.Vector2d;
import org.lwjgl.system.MemoryUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.gui.rendering.Input;
import io.cubyz.gui.rendering.Keys;
import io.cubyz.gui.rendering.Shader;

public class Button extends Component {
	//statics
	static int vao = -1;
	static Shader shader = new Shader();
	
	//state of the button
	public boolean pressed;
	public boolean hovered;
	
	//colors
	public float[] color_std 	 = 	{ 156, 166, 191}; // standart colour
	public float[] color_pressed = 	{ 146, 154, 179}; // pressed colour
	public float[] color_hovered = 	{ 156, 166, 221}; // hovered colour
	
	//shadow
	public float shadowWidth = 20;
	public float shadowHeight = 20;
	public float shadowIntensity = 0.5f;
	
	//texture
	
	
	static void initOpenGLStuff() {
		if (vao != -1)
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

		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,4*4,0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,4*4,8);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//Shader
		shader.loadFromFile("assets/cubyz/shaders/Gui/GuiButton.vs", "assets/cubyz/shaders/Gui/GuiButton.fs");
		
	}

	@Override
	public String getID() {
		return "cubyz:button";
	}

	@Override
	public void create(JsonObject object,Component parent) {
		super.create(object,parent);
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
		if(object.has("shadow")) {
			shadowWidth = object.get("shadow").getAsJsonArray().get(0).getAsFloat();
			shadowHeight = object.get("shadow").getAsJsonArray().get(1).getAsFloat();
		}
		if(object.has("shadowIntensity")) {
			shadowIntensity = object.get("shadowIntensity").getAsFloat();
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
		if(shadowWidth!=20||shadowHeight!=20) {
			JsonArray shadow = new JsonArray();
			shadow.add(shadowWidth);
			shadow.add(shadowHeight);
			
			object.add("shadow", shadow);
		}
		if(shadowIntensity!=0.5f) {
			object.addProperty("shadowIntensity", shadowIntensity);
		}
		return object;
	}

	public void update(Design design,float parentalOffsetX,float parentalOffsetY) {
		Vector2d mousepos = Input.getMousePosition(design);
		mousepos.x-=parentalOffsetX;
		mousepos.y-=parentalOffsetY;
		
		
		hovered = (left.getAsValue()<=mousepos.x&&
			top.getAsValue()<=mousepos.y&&
			left.getAsValue()+width.getAsValue()>=mousepos.x&&
			top.getAsValue()+height.getAsValue()>=mousepos.y);

		boolean old_pressed = pressed;
		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
		if(!pressed && old_pressed && scene != null && hovered)
			scene.triggerEvent(this, "button_release");
	}
	@Override
	public void draw(Design design,float parentalOffsetX,float parentalOffsetY) {
		update(design,parentalOffsetX,parentalOffsetY);
		
		//fragment
		int loc_shadow = shader.getUniformLocation("shadow");
		int loc_shadowIntensität = shader.getUniformLocation("shadowIntensity");
		int loc_mode = shader.getUniformLocation("mode");
		int loc_color = shader.getUniformLocation("color");
		
		//vertex
		int loc_scene_size = shader.getUniformLocation("scene_size");		
		int loc_position = shader.getUniformLocation("model_pos");
		int loc_size = shader.getUniformLocation("model_size");
		
		
			
		
		shader.bind();
		{
			//fragment
			glUniform2f(loc_shadow, shadowWidth/width.getAsValue(), shadowHeight/height.getAsValue());
			glUniform1f(loc_shadowIntensität, shadowIntensity);
			glUniform1i(loc_mode, (pressed?1:0));
			glUniform3fv(loc_color,hovered?(pressed?color_pressed:color_hovered):color_std);
			
			//vertex
			glUniform2f(loc_scene_size, design.width.getAsValue(), design.height.getAsValue());
			glUniform2f(loc_position,parentalOffsetX+left.getAsValue(),parentalOffsetY+top.getAsValue());
			glUniform2f(loc_size,width.getAsValue(),height.getAsValue());
			
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
	
		}
		shader.unbind();
		
		super.draw(design,parentalOffsetX,parentalOffsetY);
	}
}
