package io.cubyz.gui;

import java.util.ArrayList;


public class Scene {
	
	//data
	public String name;

	public ArrayList<Design> designs = new ArrayList<Design>();
	public ArrayList<EventListener> eventListener = new ArrayList<EventListener>();
	
	public Scene(String name) {
		this.name = name;
	}

	public Design getOptimalDesign(int width, int height) {
		// Pick the design with the closest ratio:
		float ratio = (float)width/height;
		float closest = Float.MAX_VALUE;
		Design optimalDesign = null;

		for(Design design : designs) {
			float dif = Math.abs(design.ratio() - ratio);
			if(dif < closest) {
				closest = dif;
				optimalDesign = design;
			}
		}
		
		return optimalDesign;
	}

	public void triggerEvent(Component source, String event) {
		for(EventListener listener : eventListener) {
			listener.onEvent(source, event);
		}
	}
}
