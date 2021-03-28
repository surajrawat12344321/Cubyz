package io.cubyz;

import io.cubyz.gui.Init;
import io.cubyz.gui.Window;
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
	
			while(!window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				window.render();
			}
			Log.info("Execution stopped.");
			
			System.exit(1);
		} catch(Exception e) {
			Log.severe(e);
		}
	}

}
