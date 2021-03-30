package io.cubyz.rendering;

import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import io.cubyz.gui.Component;
import io.cubyz.utils.log.Log;

import static org.lwjgl.glfw.GLFW.*;

public final class Input {
	public static Component selectedText;
	
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
					
		glfwSetKeyCallback(window.getHandle(),new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(action == GLFW_PRESS)
					real_press[key+key_count_mouse] = true;
				else if(action == GLFW_RELEASE)
					real_press[key+key_count_mouse] = false;
				
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
	}
}
