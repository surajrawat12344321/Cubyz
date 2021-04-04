package io.cubyz.gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL30;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;


public class Scene {
	
	//data
	public String name; 
	public int width=1920,height=1080;
	
	ArrayList<Component> children = new ArrayList<Component>();
	
	//methods
	public Scene(String name,int height,int width) {
		this.name = new String(name);
		this.height = height;
		this.height = width;
	}
	
	public Scene(String path) {
		loadFromFile(path);
	}
	
	public void loadFromFile(String path) {
		 try {
			 JsonParser jsonParser = new JsonParser();
			 FileReader reader = new FileReader(path);
			 //Read JSON file
		     JsonObject obj = (JsonObject) jsonParser.parse(reader);
			loadFromJson(obj);
		} catch (Exception e) {
			Log.severe(e);
		}
	}
	public void saveAsFile(String path) {
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(saveAsJson().toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.warning(e);
		}
	}
	public void loadFromJson(JsonObject scene) {
		height = scene.getAsJsonPrimitive("height").getAsInt();
		width = scene.getAsJsonPrimitive("width").getAsInt();
		name = scene.getAsJsonPrimitive("name").getAsString();
		
		JsonArray jchildren = scene.getAsJsonArray("children");
		for (JsonElement jsonElement : jchildren) {
			JsonObject jsonObject = (JsonObject)jsonElement;
			children.add(ComponentRegistry.createByJson(jsonObject));
		}
	}
	public JsonObject saveAsJson() {
		JsonObject scene =  new JsonObject();
		scene.addProperty("name", name);
		scene.addProperty("height", height);
		scene.addProperty("width", width);
		
		JsonArray jchildren = new JsonArray();
		for (Component guiElement : children) {
			jchildren.add(guiElement.toJson());
		}
		scene.add("children", jchildren);
		
		return scene;
	}
	float getRatio() {
		return (float)width/height;
	}
	public void add(Component component) {
		children.add(component);
	}
	public void draw() {
		GL30.glDisable(GL_DEPTH_TEST);
		
		for (Component component : children) {
			component.draw(this);
		}
		GL30.glEnable(GL_DEPTH_TEST);
	}
	
	public float ratio() {
		return (float)width/height;
	}
}
