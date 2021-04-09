package vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.vulkan.*;
import org.lwjgl.vulkan.VK11.*;

import io.cubyz.utils.settings.SettingsConstants;
import io.cubyz.utils.settings.elements.SettingVector2f;
import io.cubyz.utils.settings.Settings;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.VK11.*;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;

public final class Window {

	

	private static long handle = -1;
	private static int width;
	private static int height;
	private static VkInstance instance;
	
	private static boolean init = false;
	
	public static void create() {
		/**
		 * Check if there is already a window. And then destroy the old window first,before recreating a new one.
		 * and init glfw if nessary.
		 */
		dispose();
		if(!init) {
			init = true;
			glfwInit();	
		}
		
		/**
		 * Create Window without creating a OpenGL context. 
		 * And without other OpenGL orientated features (e.g adjusting OpenGL when resizing the window)
		 */
		//TODO change the monitor where the window is drawn to.
	    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
	    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
	    
	    SettingVector2f windowSize = (SettingVector2f)Settings.get(SettingsConstants.WINDOW_SIZE);
	    windowSize.checkUp("vector2f");
	    width = (int) windowSize.x;
	    height = (int) windowSize.y;
	    
	    handle = glfwCreateWindow(width,height, "Vulkan window", 0, 0);

	  
	}
	public static void create(float width,float height) {
		Settings.set(SettingsConstants.WINDOW_SIZE, new SettingVector2f(width,height));
		create();
		
	}
	/**
	 * updates the window
	 * @return is the window still open
	 */
	public static boolean update() {
		if(glfwWindowShouldClose(handle)) {
			dispose();	
			return false;	
		}
		
		glfwPollEvents();
		
		return true;
	}
	/**
	 * disposed the window if possible
	 */
	public static void dispose() {
		if(handle==-1)
			return;
		handle = -1;
		init = false;
		//glfwDestroyWindow(handle);
		glfwTerminate();
	}
	
	public void createInstance() {
		/**
		 * [Optional] Give Vulkan the Engine and Game version  (1.0.0) and name
		 */
		VkApplicationInfo appinfo = new VkApplicationInfo(null);
		appinfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
		appinfo.pApplicationName(ByteBuffer.wrap(("Cubyz").getBytes()));
		appinfo.applicationVersion(VK_MAKE_VERSION(1,0,0));
		appinfo.pEngineName(ByteBuffer.wrap("Cubyz Engine".getBytes()));
		appinfo.engineVersion(VK_MAKE_VERSION(1,0,0));
		appinfo.apiVersion(VK_API_VERSION_1_0);
		
		/**
		 * Vulkan driver which global extensions and validation layers we want
		 */
		VkInstanceCreateInfo createInfo = new VkInstanceCreateInfo(null);
		createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
		createInfo.pApplicationInfo(appinfo);
		/**
		  	in c++ we would need  the following,but i couldn't find the java aquivalence;
		 	
		 	uint32_t glfwExtensionCount = 0;
			const char** glfwExtensions;
			glfwExtensions = glfwGetRequiredInstanceExtensions(&glfwExtensionCount);
			createInfo.enabledExtensionCount = glfwExtensionCount;
		 */
		createInfo.ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions());
		//c++ 	createInfo.enabledLayerCount = 0;
		// c++ ????? vkCreateInstance(createInfo, null,instance);
	}

}
