package io.cubyz.gui.element;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.sql.Time;

import org.lwjgl.system.MemoryUtil;

import io.cubyz.gui.Component;
import io.cubyz.gui.Scene;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Keys;
import io.cubyz.rendering.Shader;

import static org.lwjgl.glfw.GLFW.*;

public class Picture extends Component {

	static int vbo = -1;
	static Shader shader = new Shader();

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
		return "Cubyz:Picture";
	}

	private int[] colour = { 156, 166, 191, // standart colour
			146, 154, 179, // pressed colour
			156, 166, 221 // hovered colour
	};

	private boolean pressed;
	private boolean hovered;

	private Runnable onAction;

	public void setOnAction(Runnable onAction) {
		this.onAction = onAction;
	}

	public Picture(int left, int top, int width, int height) {
		super(left, top, width, height);
		initOpenGLStuff();

		
	}

	@Override
	public void draw(Scene scene) {

		int loc_shadow = shader.getUniformLocation("shadow");
		int loc_mode = shader.getUniformLocation("mode");
		int loc_translation = shader.getUniformLocation("translation");
		int loc_shadowIntensität = shader.getUniformLocation("shadowIntensity");
		
		Input.setVirtualKeyFromGLFWMouse(Keys.CUBYZ_GUI_PRESS_PRIMARY,GLFW_MOUSE_BUTTON_1);
		
		pressed = Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY);
			
		
		shader.bind();
		glUniform2f(loc_shadow, 20.f/scene.width, 20.f/scene.height);
		glUniform2f(loc_translation,-1+left/scene.width,1-top/scene.height);
		glUniform1i(loc_mode, (pressed?1:0));
		glUniform1f(loc_shadowIntensität, 0.5f);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		shader.unbind();
	}
	

}
