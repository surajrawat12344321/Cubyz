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

import io.cubyz.rendering.CubyzGraphics2D;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Keys;
import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;

/**
	{@code Scene} design for a specific screen ratio.
*/
public class Design extends Component{
	
	@Override
	public String getID() {
		return "Cubyz:Design";
	}
	
	//methods
	public Design(String name,int height,int width) {
		this.name = new String(name);
		this.width.setAsValue(width);
		this.height.setAsValue(height);
		this.parent = this;
	}
	
	public Design(String path) {
		loadFromFile(path);
	}
	
	public void loadFromFile(String path) {
		try {
			JsonParser jsonParser = new JsonParser();
			FileReader reader = new FileReader(path);
			//Read JSON file
		    JsonObject obj = (JsonObject) jsonParser.parse(reader);
			create(obj,this);
		} catch (Exception e) {
			Log.severe(e);
		}
	}

	public void saveAsFile(String path) {
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(toJson().toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.warning(e);
		}
	}
	@Override
	public void create(JsonObject design,Component parent) {
		height.setAsValue(design.getAsJsonPrimitive("height").getAsInt());
		width.setAsValue(design.getAsJsonPrimitive("width").getAsInt());
		name = design.getAsJsonPrimitive("name").getAsString();

		children.clear();
		JsonArray jchildren = design.getAsJsonArray("children");
		for (JsonElement jsonElement : jchildren) {
			JsonObject jsonObject = (JsonObject)jsonElement;
			children.add(ComponentRegistry.createByJson(jsonObject,this));
		}
		
		// Get the fitting scene and give it all of its children:
		Scene scene = SceneManager.getScene(name);
		for(Component child : children) {
			child.setScene(scene);
		}
	}
	@Override
	public JsonObject toJson() {
		JsonObject scene =  new JsonObject();
		scene.addProperty("name", name);
		scene.add("height", height.toJson());
		scene.add("width", width.toJson());
		
		JsonArray jchildren = new JsonArray();
		for (Component guiElement : children) {
			jchildren.add(guiElement.toJson());
		}
		scene.add("children", jchildren);
		
		return scene;
	}
	public void draw() {
		//deselect everythink if someone pressed somewhere else
		if(Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY))
			Input.selectedText = null;
	
		
		GL30.glDisable(GL_DEPTH_TEST);
	
		CubyzGraphics2D.instance.design = this;
		for (Component component : children) {
			component.draw(this,0,0);
		}
	
		GL30.glEnable(GL_DEPTH_TEST);
	}
	
	public void setScene(Scene scene) {
		Log.warning("A scene is a scene.You can't set the scene of a scene.");
	}
	/**
	  @return ratio of width/height
	*/
	public float ratio() {
		return (float)width.getAsValue()/height.getAsValue();
	}
}
