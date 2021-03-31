package io.cubyz.gui.element;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.sql.Time;

import org.joml.Vector2d;
import org.lwjgl.system.MemoryUtil;

import com.google.gson.JsonObject;

import io.cubyz.gui.Component;
import io.cubyz.gui.Scene;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Keys;
import io.cubyz.rendering.Shader;

import static org.lwjgl.glfw.GLFW.*;

public class Picture extends Component {

	static int vbo = -1;
	static Shader shader = new Shader();
	private boolean pressed;
	private boolean hovered;
	public Runnable onAction;
	private float[] color_std 	  = 	{ 156, 166, 191}; // standart colour
	private float[] color_pressed = 	{ 146, 154, 179}; // pressed colour
	private float[] color_hovered = 	{ 156, 166, 221}; // hovered colour
	
	
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
		shader.loadFromFile("assets/cubyz/shaders/Gui/GuiPicture.vs", "assets/cubyz/shaders/Gui/GuiPicture.fs");
		
	}

	@Override
	public String getID() {
		return "cubyz:picture";
	}
	
	public void setOnAction(Runnable onAction) {
		this.onAction = onAction;
	}

	@Override
	public void create(JsonObject object) {
		super.create(object);
		initOpenGLStuff();
	}
	@Override
	public JsonObject toJson() {
		JsonObject obj = super.toJson();
		return obj;
	}

	public void update(Scene scene) {
		Vector2d mousepos = Input.getMousePosition(scene);
		
		hovered = (left<=mousepos.x&&
			top<=mousepos.y&&
			left+width>=mousepos.x&&
			top+height>=mousepos.y);

		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
	}
	@Override
	public void draw(Scene scene) {
		update(scene);
		
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
			float borderWidth = 20;
			float borderHeight = 20;
			
			glUniform2f(loc_shadow, borderWidth/width, borderHeight/height);
			glUniform1f(loc_shadowIntensität, 0.5f);
			glUniform1i(loc_mode, (pressed?1:0));
			glUniform3fv(loc_color,hovered?(pressed?color_pressed:color_hovered):color_std);
			
			//vertex
			
			glUniform2f(loc_scene_size, scene.width, scene.height);
			glUniform2f(loc_position,left,top);
			glUniform2f(loc_size,width,height);
			
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
	
		}
		shader.unbind();
	}
	

}
