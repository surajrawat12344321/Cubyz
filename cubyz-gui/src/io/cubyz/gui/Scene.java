package io.cubyz.gui;

import java.util.ArrayList;

import io.cubyz.gui.rendering.Window;


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
	public void update() {
		if(currentDesign == null) {
			getOptimalDesign(Window.width, Window.height);
		}
		if(currentDesign == null) return;
			currentDesign.update();
	}

	public void triggerEvent(Component source, String event) {
		for(EventListener listener : eventListener) {
			listener.onEvent(source, event);
		}
	}
}
