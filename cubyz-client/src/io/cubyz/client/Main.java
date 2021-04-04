package io.cubyz.client;

import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.cubyz.gui.Component;
import io.cubyz.rendering.GraphicFont;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Window;
import io.cubyz.gui.Design;
import io.cubyz.gui.Init;
import io.cubyz.gui.Scene;
import io.cubyz.gui.SceneManager;
import io.cubyz.utils.log.Log;

/**
 *	Starting point of the client.
 *
 */
public class Main {
	public static void main(String[] args) {
		
		
		try {
			Init.init();
			
			Window window = new Window(960, 540);
			window.setBackgroundColor(0.8f, 0.8f, 1, 1);
	
			
			Design testDesign = new Design("cubyz-client/testScene.json");
			Scene scene = SceneManager.getScene("test");
			scene.designs.add(testDesign);
			
			
			while(!window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				scene.draw();
				Input.update();
				window.render();
			}
			Log.info("Execution stopped.");
			
			System.exit(1);
		} catch(Exception e) {
			Log.severe(e);
		}
	}

}
