package io.cubyz.rendering;

import static org.lwjgl.opengl.GL30.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import io.cubyz.gui.Component;
import io.cubyz.gui.Design;
import io.cubyz.gui.element.Text;
import io.cubyz.utils.log.Log;

import static org.lwjgl.glfw.GLFW.*;

public final class Input {
	//public
	public static Component selectedText;
	
	//window
	static Window window;
	
	private static Vector2d mousePosition = new Vector2d();
	public static Vector2d getMousePosition(Design design) {
		Vector2d output = new Vector2d();
		output.x = mousePosition.x/Window.width*design.width.getAsValue();
		output.y = mousePosition.y/Window.height*design.height.getAsValue();
		
		return output;
	}
	
	//which real keys are pressed
	private static final int key_count_mouse = 200;
	private static final int key_count_keyboard = 65536;
	private static boolean real_press[] = new boolean[key_count_mouse+key_count_keyboard];
	
	//maps virtualkey to realkey
	private static HashMap<String, Integer> virtualkeys = new HashMap<String,Integer>();
	
	public static boolean pressed(String vKey) {
		return real_press[virtualkeys.get(vKey)];
	}
	public static void setVirtualKeyFromGLFWKeyboard(String vKey,int realKey) {
		virtualkeys.put(vKey, realKey+key_count_mouse);
	}
	public static void setVirtualKeyFromGLFWMouse(String vKey,int realKey) {
		if(realKey>=key_count_mouse){
			Log.severe("unacceptable mouse code");
		}
		else virtualkeys.put(vKey, realKey);
	}
	
	
	static void set(Window window) {
		Input.setVirtualKeyFromGLFWMouse(Keys.CUBYZ_GUI_PRESS_PRIMARY,GLFW_MOUSE_BUTTON_1);
		
		Input.window = window;
		
		
		glfwSetKeyCallback(window.getHandle(),new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {					
				if(action == GLFW_PRESS)
					real_press[key+key_count_mouse] = true;
				else if(action == GLFW_RELEASE)
					real_press[key+key_count_mouse] = false;
				
				
				//special keys for text
				if((action==GLFW_PRESS||action==GLFW_REPEAT)&&(selectedText!=null?selectedText.getID()=="cubyz:text":false)) {
					if(key == GLFW_KEY_BACKSPACE) {
						if(((Text)selectedText).editable)
							((Text)selectedText).deleteTextAtCursor();
					}else if(key == GLFW_KEY_LEFT) 
						((Text)selectedText).moveCursor(-1);
					else if(key == GLFW_KEY_RIGHT) 
						((Text)selectedText).moveCursor(1);
				}
				
			}
		});
		glfwSetMouseButtonCallback(window.getHandle(), new GLFWMouseButtonCallback() {
			
			@Override
			public void invoke(long window, int button, int action, int mods) {
					if(action == GLFW_PRESS)
					real_press[button] = true;
				else if(action == GLFW_RELEASE)
					real_press[button] = false;
			}
		});
		
		glfwSetCursorPosCallback(window.getHandle(),new GLFWCursorPosCallback() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				// TODO Auto-generated method stub
				mousePosition.x = xpos;
				mousePosition.y = ypos;
			}
		});
		glfwSetCharCallback(window.getHandle(),new GLFWCharCallback() {
			
			@Override
			public void invoke(long window, int codepoint) {
				if(selectedText!=null) {
					if(selectedText.getID()=="cubyz:text"){
						if(((Text)selectedText).editable){	
							((Text)selectedText).addTextAtCursor(""+(char)codepoint);
						}
					}
				}
			}
		});
	}
	public static void update() {
		DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window.getHandle(), xBuffer, yBuffer);
		mousePosition.x  = xBuffer.get(0);
		mousePosition.y = yBuffer.get(0);
	}
}
