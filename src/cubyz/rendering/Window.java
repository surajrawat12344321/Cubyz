package cubyz.rendering;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.system.MemoryUtil;

import cubyz.Settings;
import cubyz.server.Constants;

public class Window {
	public static long handle;
	
	private static boolean fullscreen = false;
	
	static Vector3f backgroundColor = new Vector3f(0, 0, 0);
	public static final Matrix4f projectionMatrix = new Matrix4f();
	
	public static int width,height;
	
	public static void createWindow(int width, int height) {
		Window.width = width;
		Window.height = height;
		
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		handle = glfwCreateWindow(width, height, Constants.name+" "+Constants.version, MemoryUtil.NULL, MemoryUtil.NULL);
		// Check if an error occured in initialization:
		if(handle == MemoryUtil.NULL) {
			int errorCode = glfwGetError(PointerBuffer.allocateDirect(1));
			throw new RuntimeException("Failed to create the GLFW window (code = " + errorCode + ")");
		}
		
		// Create the opengl context:
		glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		
		// Generate the projection matrix:
		regenerateMatrix(Window.width, Window.height);
		
		glfwShowWindow(handle);
		
		glfwSetFramebufferSizeCallback(handle, (window, newWidth, newHeight) -> {
			Window.width = newWidth;
			Window.height = newHeight;
		    regenerateMatrix(Window.width, Window.height);
		});
		
		Input.init();
	}
	
	public static void setBackgroundColor(float red, float green, float blue, float alpha) {
		glClearColor(red, green, blue, alpha);
	}
	
	/**
	 * Update the matrix for the given window size.
	 * @param width
	 * @param height
	 */
	public static void regenerateMatrix(int width, int height) {
		if(height==0)
			return;
		glViewport(0, 0, width, height);
		float aspectRatio = width / (float)height;
		projectionMatrix.identity();
		projectionMatrix.perspective((float)Math.toRadians(Settings.FOV), aspectRatio, Settings.Z_NEAR, Settings.Z_FAR);
	}
	
	/**
	 * @return Whether the window should close due to user action(such as clicking the close widget in the corner of the window).
	 */
	public static boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}
	
	public static void render() {
		// Set some gl parameters. These can be changed by nanoVG.
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glfwSwapBuffers(handle);
		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * Toggles fullscreen.
	 */
	public static void toggleFullscreen() {
		fullscreen = !fullscreen;
		if (fullscreen) {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			int width = gd.getDisplayMode().getWidth();
			int height = gd.getDisplayMode().getHeight();
			glfwSetWindowMonitor(handle, glfwGetPrimaryMonitor(), 0, 0, width, height, GLFW_DONT_CARE);
		} else {
			// Get the resolution of the primary monitor
			GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowMonitor(handle, MemoryUtil.NULL, (vidMode.width() - width)/2, (vidMode.height() - height)/2, width, height, GLFW_DONT_CARE);
			glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_TRUE);
		}
	}
	/*
	 * get the handle of the window
	 * */
	public static long getHandle() {
		return handle;
	}
	
}
