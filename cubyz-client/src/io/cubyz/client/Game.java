package io.cubyz.client;

import io.cubyz.gui.Component;
import io.cubyz.gui.EventListener;
import io.cubyz.gui.SceneManager;
import io.cubyz.world.UniverseInterface;

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
					SceneManager.setCurrentScene("mainMenu");
				}
			}
		});
	}
}
