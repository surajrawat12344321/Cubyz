package cubyz.client;

import cubyz.client.renderUniverse.RenderWorld;
import cubyz.client.renderUniverse.TextureAtlas;
import cubyz.gui.Init;
import cubyz.gui.SceneManager;
import cubyz.rendering.Input;
import cubyz.rendering.Window;
import cubyz.server.BaseMod;
import cubyz.server.modding.ModLoader;
import cubyz.utils.gui.StatusInfo;
import cubyz.utils.log.Log;

/**
 *	Starting point of the client.
 *
 */
public class Main {
	public static void main(String[] args) {
		
		ModLoader loader = new ModLoader(null, new StatusInfo(), new BaseMod(), new ClientBaseMod());
		try {
			Init.init();
			
			Window.createWindow(1280, 720);
			Window.setBackgroundColor(0.5f, 1f, 0.5f, 1);
			SceneManager.init();
			Game.init();
			
			// Output the texture atlas to a file.
			TextureAtlas.BLOCKS.write();
			
			while(!Window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				Input.update();
				RenderWorld.render();
				SceneManager.update();
				SceneManager.draw();
				Window.render();
			}
			Log.info("Execution stopped.");
			
			System.exit(1);
		} catch(Exception e) {
			Log.severe(e);
		}
	}

}
