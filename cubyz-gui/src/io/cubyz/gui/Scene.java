package io.cubyz.gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;


public class Scene{
	
	//data
	public String name; 
	public int left=0,top=0,width=1080,height=720;
	
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
		 Gson gson = new Gson();   
		 try {
			Scene scene = gson.fromJson(new FileReader(path), Scene.class);
			
			left = scene.left;
			top = scene.top;
			height = scene.height;
			width = scene.width;
			name = scene.name;
			children = scene.children;
		    
		} catch (Exception e) {
			Log.severe(e);
		}
	}
	public void saveAsFile(String path) {
		Gson gson = new Gson();
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(gson.toJson(this));
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.warning(e);
		}
	}
	
	float getRatio() {
		return (float)width/height;
	}
	public void add(Component component) {
		children.add(component);
	}
	public void draw() {
		for (Component component : children) {
			component.draw(this);
		}
	}
}
