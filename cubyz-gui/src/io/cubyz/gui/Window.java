package io.cubyz.gui;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryUtil;

import io.cubyz.Settings;
import io.cubyz.server.Constants;

public class Window extends Container {
	private final long handle;
	
	private boolean fullscreen = false;
	
	Vector3f backgroundColor = new Vector3f(0, 0, 0);
	Matrix4f projectionMatrix = new Matrix4f();
	
	public Window(int width, int height) {
		super(0, 0, width, height);
		this.width = width;
		this.height = height;
		
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
		regenerateMatrix(this.width, this.height);
		
		glfwShowWindow(handle);
		
		glfwSetFramebufferSizeCallback(handle, (window, newWidth, newHeight) -> {
		    this.width = newWidth;
		    this.height = newHeight;
		    regenerateMatrix(this.width, this.height);
		});
	}
	
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}
	
	/**
	 * Update the matrix for the given window size.
	 * @param width
	 * @param height
	 */
	public void regenerateMatrix(int width, int height) {
		GL11C.glViewport(0, 0, width, height);
		float aspectRatio = width / height;
		projectionMatrix.identity();
		projectionMatrix.perspective((float)Math.toRadians(Settings.FOV), aspectRatio, Settings.Z_NEAR, Settings.Z_FAR);
	}
	
	/**
	 * @return Whether the window should close due to user action(such as clicking the close widget in the corner of the window).
	 */
	public boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}
	
	public void render() {
		// Set some gl parameters. These can be changed by nanoVG.
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		glfwSwapBuffers(handle);
		glfwPollEvents();
	}
	
	/**
	 * Toggles fullscreen.
	 */
	public void toggleFullscreen() {
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
}
