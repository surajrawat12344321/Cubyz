package io.cubyz.client;

import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.cubyz.gui.Component;
import io.cubyz.gui.element.Picture;
import io.cubyz.rendering.Input;
import io.cubyz.rendering.Window;
import io.cubyz.gui.Init;
import io.cubyz.gui.Scene;
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
			window.setBackgroundColor(1, 0, 1, 1);
	
			
			Scene scene = new Scene("cubyz-client/testScene.json");//new Scene("name",720,1080);
			
			//Component button = new Picture();
			//button.create((JsonObject) JsonParser.parseString("{'left':'480','top':'415','width':'490','height':'290'}"));
			//scene.add(button);
			
			//scene.saveAsFile("cubyz-client/testScene.json");
			
			
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
