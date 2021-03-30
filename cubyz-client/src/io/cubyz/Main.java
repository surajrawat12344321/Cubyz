package io.cubyz;

import java.io.FileWriter;

import com.google.gson.Gson;

import io.cubyz.gui.Component;
import io.cubyz.gui.element.Picture;
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
	
			
			Scene scene = new Scene("name",720,1080);
			scene.add(new Picture(245,145,490,290));
			
			//scene.saveAsFile("cubyz-client/testScene.json");
			
			
			while(!window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				scene.draw();
				window.render();
			}
			Log.info("Execution stopped.");
			
			System.exit(1);
		} catch(Exception e) {
			Log.severe(e);
		}
	}

}
