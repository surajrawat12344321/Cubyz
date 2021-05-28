package io.cubyz.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import io.cubyz.utils.log.Log;

public class SceneManager {
	public static HashMap<String, Scene> scenes = new HashMap<String,Scene>();
	public static Scene currentScene;
	
	public static void addDesign(String path) {
		Design testDesign = new Design(path);
		Scene scene = SceneManager.getScene(testDesign.name);
		scene.designs.add(testDesign);
	}
	/**
	 * loads all designes out of the assets\cubyz\design folder
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
	}
	public static void draw() {
		if(currentScene!=null)
			currentScene.draw();
	}
	public static void update() {
		if(currentScene!=null)
			currentScene.update();
	}
}