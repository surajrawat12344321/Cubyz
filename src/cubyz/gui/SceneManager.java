package cubyz.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.glBlendEquationSeparate;

import java.io.File;
import java.util.HashMap;

import cubyz.gui.rendering.Window;

public class SceneManager {
	public static HashMap<String, Scene> scenes = new HashMap<String,Scene>();
	public static Scene currentScene;
	public static Design currentDesign;
	
	public static void addDesign(String path) {
		Design testDesign = new Design(path);
		Scene scene = SceneManager.getScene(testDesign.name);
		scene.designs.add(testDesign);
	}
	/**
	 * loads all designs out of the assets\cubyz\design folder
	 */
	public static void init() {
		File folder = new File("assets/cubyz/design");
		for (final File fileEntry : folder.listFiles()) {
			addDesign(fileEntry.getPath());
	    }
	}
	public static Scene getScene(String name) {
		if(!scenes.containsKey(name)) {
			scenes.put(name, new Scene(name));
		}
		return scenes.get(name);
	}
	public static void setCurrentScene(String name) {
		currentScene = scenes.get(name);
		updateDesign();
	}
	public static void updateDesign() {
		if(currentScene == null) {
			currentDesign = null;
		} else {
			currentDesign = currentScene.getOptimalDesign(Window.width, Window.height);
		}
	}
	public static void draw() {
		// Adjust the alpha blending function which gets changed by the renderer:
		glEnable(GL_BLEND);
		glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		if(currentDesign!=null)
			currentDesign.draw();
	}
	public static void update() {
		if(currentDesign!=null)
			currentDesign.update();
	}
	public static void triggerEvent(Component source, String event) {
		currentScene.triggerEvent(source, event);
	}
}