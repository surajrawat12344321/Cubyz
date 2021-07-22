package cubyz.gui.element;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.ArrayList;

import org.joml.Vector2d;
import org.lwjgl.system.MemoryUtil;

import cubyz.gui.Component;
import cubyz.gui.Design;
import cubyz.gui.SceneManager;
import cubyz.gui.rendering.Input;
import cubyz.gui.rendering.Keys;
import cubyz.gui.rendering.Shader;
import cubyz.gui.rendering.Texture;
import cubyz.gui.rendering.TextureManager;
import cubyz.gui.text.Text;
import cubyz.utils.json.*;

public class Button extends Component {
	//statics
	static int vao = -1;
	static final Shader shader = Shader.loadFromFile("assets/cubyz/shaders/Gui/GuiButton.vs", "assets/cubyz/shaders/Gui/GuiButton.fs");
	
	//state of the button
	public boolean pressed;
	public boolean hovered;
	public boolean release = false;
	
	//colors
	public float[] color_std 	 = 	{156, 166, 191, 255}; // standard color
	public float[] color_pressed = 	{146, 154, 179, 255}; // pressed color
	public float[] color_hovered = 	{156, 166, 221, 255}; // hovered color
	
	//shadow
	public float shadowWidth = 20;
	public float shadowHeight = 20;
	public float shadowIntensity;
	
	//texture
	private Texture texture = null;
	private String texture_path = "";
	
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
		
	}

	@Override
	public String getID() {
		return "cubyz:button";
	}

	public void setTexture(String path) {
		texture_path = path;
		texture = TextureManager.require(texture_path);
	}
	
	@Override
	public void create(JsonObject object,Component parent) {
		super.create(object,parent);
		initOpenGLStuff();
		object.getArrayNoNull("color").getFloats(color_std);
		object.getArrayNoNull("colorHovered").getFloats(color_hovered);
		object.getArrayNoNull("colorPressed").getFloats(color_pressed);
		ArrayList<JsonElement> jsonArray = object.getArrayNoNull("shadow").array;
		if(jsonArray.size() >= 2) {
			shadowWidth = jsonArray.get(0).getFloat(20);
			shadowHeight = jsonArray.get(1).getFloat(20);
		}
		shadowIntensity = object.getFloat("shadowIntensity", 0.5f);
		texture_path = object.getString("texture", "");
		if(texture_path.length() != 0)
			texture = TextureManager.require(texture_path);

		String value = object.getString("text", "");
		if(value.length() != 0) {
			Text text = new Text();
			text.create(new JsonObject(), this);
			text.width.setAsPercentage(0.7f, width);
			text.height.setAsPercentage(0.7f, height);
			text.left.setAsPercentage(0.5f, width);
			text.top.setAsPercentage(0.5f, height);
			text.originTop.setAsPercentage(0.5f, text.height);
			text.originLeft.setAsPercentage(0.5f, text.width);
			text.setText(value);
			
			add(text);
		}

	}
	
	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		
		if(!Arrays.equals(color_std,new float[]{156, 166, 191})) {
			JsonArray color = new JsonArray();
			color.addFloats(color_std);
			object.map.put("color", color);
		}
		if(!Arrays.equals(color_hovered,new float[]{156, 166, 221})) {
			JsonArray color = new JsonArray();
			color.addFloats(color_hovered);
			object.map.put("colorHovered", color);
		}
		if(!Arrays.equals(color_pressed,new float[]{146, 154, 179})) {
			JsonArray color = new JsonArray();
			color.addFloats(color_pressed);
			object.map.put("colorPressed", color);
		}
		if(shadowWidth!=20||shadowHeight!=20) {
			JsonArray shadow = new JsonArray();
			shadow.addFloats(shadowWidth, shadowHeight);
			object.map.put("shadow", shadow);
		}
		if(shadowIntensity!=0.5f) {
			object.put("shadowIntensity", shadowIntensity);
		}
		if(texture!=null) {
			object.put("texture", texture_path);			
		}
		return object;
	}
	@Override
	public void update(Design design,float parentalOffsetX,float parentalOffsetY) {
		super.update(design,parentalOffsetX,parentalOffsetY);
		
		Vector2d mousepos = Input.getMousePosition(design);
		mousepos.x-=parentalOffsetX;
		mousepos.y-=parentalOffsetY;
		
		
		hovered = (left.getAsValue()<=mousepos.x&&
			top.getAsValue()<=mousepos.y&&
			left.getAsValue()+width.getAsValue()>=mousepos.x&&
			top.getAsValue()+height.getAsValue()>=mousepos.y)&&
				design.hovered==null;
		if(hovered)
			design.hovered = this;
		
		boolean old_pressed = pressed;
		pressed = hovered?Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY):false;
		
		if(!pressed && old_pressed && hovered) {
			SceneManager.triggerEvent(this, "button_release");
			release = true;		
		}else release = false;
	}
	@Override
	public void draw(Design design,float parentalOffsetX,float parentalOffsetY) {
		
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
			glUniform4fv(loc_color,hovered?(pressed?color_pressed:color_hovered):color_std);
			
			//vertex
			glUniform2f(loc_scene_size, design.width.getAsValue(), design.height.getAsValue());
			glUniform2f(loc_position,parentalOffsetX+left.getAsValue(),parentalOffsetY+top.getAsValue());
			glUniform2f(loc_size,width.getAsValue(),height.getAsValue());
			
			if(texture!=null)
				texture.bind();
			else 
				TextureManager.white().bind();
			
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
	
		}
		shader.unbind();
		
		super.draw(design,parentalOffsetX,parentalOffsetY);
	}
}
