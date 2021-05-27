package io.cubyz.client;

import io.cubyz.gui.Init;
import io.cubyz.gui.SceneManager;
import io.cubyz.gui.rendering.Input;
import io.cubyz.gui.rendering.Window;
import io.cubyz.utils.log.Log;

/**
 *	Starting point of the client.
 *
 */
public class Main {
	public static void main(String[] args) {
		
		
		try {
			Init.init();
			
			Window window = new Window(1280, 720);
			window.setBackgroundColor(0.5f, 1f, 0.5f, 1);
			
			SceneManager.init();
			Game.init();
			
			while(!window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				Input.update();
				SceneManager.draw();
				RenderUniverse.draw();
				window.render();
			}
			Log.info("Execution stopped.");
			
			System.exit(1);
		} catch(Exception e) {
			Log.severe(e);
		}
	}

}
