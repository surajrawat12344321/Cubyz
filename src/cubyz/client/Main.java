package cubyz.client;

import cubyz.client.renderUniverse.RenderUniverse;
import cubyz.client.renderUniverse.TextureAtlas;
import cubyz.gui.Init;
import cubyz.gui.SceneManager;
import cubyz.gui.rendering.Input;
import cubyz.gui.rendering.Window;
import cubyz.server.BaseMod;
import cubyz.server.modding.ModLoader;
import cubyz.utils.gui.StatusInfo;
import cubyz.utils.log.Log;
import cubyz.world.World;
import cubyz.world.terrain.MapGenerator;

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
			new MapGenerator(new World(), 0, 0, 1);
			SceneManager.init();
			Game.init();
			
			// Output the texture atlas to a file.
			TextureAtlas.BLOCKS.write();
			
			while(!Window.shouldClose()) {
				try{
					Thread.sleep(10);
				} catch(Exception e) {}
				Input.update();
				RenderUniverse.draw();
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
