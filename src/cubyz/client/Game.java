package cubyz.client;

import cubyz.client.renderUniverse.RenderUniverse;
import cubyz.gui.Component;
import cubyz.gui.EventListener;
import cubyz.gui.SceneManager;
import cubyz.world.Universe;
import cubyz.world.UniverseInterface;

/**
 * 
 *  Where the Game is.
 *
 */
public class Game {
	public static UniverseInterface connection;
	/**
	 * for joining a server:		connection = new UniverseServerConnection(ip etc.);
	 * for joining a local world:	connection = new Universe(path etc.);
	 */
	public static void init(){
		SceneManager.setCurrentScene("mainMenu");
		SceneManager.getScene("mainMenu").eventListener.add(new EventListener() {	
			@Override public void onEvent(Component source, String event) {
				if(source.name.equals("singleplayer")&&event.equals("button_release")) {
					SceneManager.setCurrentScene("singleplayerMenu");
				}
			}
		});
		SceneManager.getScene("singleplayerMenu").eventListener.add(new EventListener() {	
			@Override public void onEvent(Component source, String event) {
				if(source.name.equals("logo")&&event.equals("button_release")) {
					//SceneManager.setCurrentScene("mainMenu");
					RenderUniverse.universe = new Universe();
				}
			}
		});
	}
}
