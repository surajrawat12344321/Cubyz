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

import io.cubyz.rendering.Window;

import io.cubyz.utils.datastructures.Registry;
import io.cubyz.utils.log.Log;


public class Scene {
	
	//data
	public String name;

	public ArrayList<Design> designs = new ArrayList<Design>();
	public ArrayList<EventListener> eventListener = new ArrayList<EventListener>();

	public Design currentDesign = null;
	
	public Scene(String name) {
		this.name = name;
	}

	public void getOptimalDesign(int width, int height) {
		// Pick the design with the closest ratio:
		float ratio = (float)width/height;
		float closest = Float.MAX_VALUE;

		for(Design design : designs) {
			float dif = Math.abs(design.ratio() - ratio);
			if(dif < closest) {
				closest = dif;
				currentDesign = design;
			}
		}
	}

	public void draw() {
		if(currentDesign == null) {
			getOptimalDesign(Window.width, Window.height);
		}
		if(currentDesign == null) return;
		currentDesign.draw();
	}

	public void triggerEvent(Component source, String event) {
		for(EventListener listener : eventListener) {
			listener.onEvent(source, event);
		}
	}
}
