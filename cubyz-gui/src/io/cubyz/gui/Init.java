package io.cubyz.gui;

import javax.swing.JOptionPane;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Library;

import io.cubyz.utils.log.Log;

/**
 * Responsible for initializing all the lwjgl stuff.
 */

public class Init {
	/**
	 * Initializes lwjgl.
	 */
	public static void init() {
		// initialize LWJGL libraries to be able to catch any potential errors (like missing library).
		try {
			Library.initialize();
		} catch (UnsatisfiedLinkError e) {
			Log.severe("Missing LWJGL libraries for " + 
					System.getProperty("os.name") + " on " + System.getProperty("os.arch"));
			JOptionPane.showMessageDialog(null, "Missing LWJGL libraries for " + 
					System.getProperty("os.name") + " on " + System.getProperty("os.arch"), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		// Initialize glfw:
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
	}
}
