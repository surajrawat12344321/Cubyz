package cubyz.client;

import java.security.SecureRandom;
import java.util.UUID;

import cubyz.client.renderUniverse.RenderWorld;
import cubyz.gui.Component;
import cubyz.gui.EventListener;
import cubyz.gui.SceneManager;
import cubyz.rendering.Input;
import cubyz.world.World;
import cubyz.world.WorldInterface;

/**
 * 
 *  Where the Game is.
 *
 */
public class Game {
	public static WorldInterface connection;
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
					SceneManager.setCurrentScene("inGame");
					Input.grabMouse();
					RenderWorld.world = new World();
					UUID newID = UUID.randomUUID();
					SecureRandom r = new SecureRandom();
					char[] key = new char[32 + r.nextInt(16)];
					for(int i = 0; i < key.length; i++){
						key[i] = (char)(r.nextInt() & 0xffff);
					}
					// TODO: Store and access authentication data.
					System.out.println("Authenticating: "+RenderWorld.world.authenticate(newID, key, "Your Name"));
					System.out.println("Authenticating: "+RenderWorld.world.authenticate(newID, key, "Your Name"));
					key[0] ^= 1;
					System.out.println("Authenticating: "+RenderWorld.world.authenticate(newID, key, "Your Name"));
				}
			}
		});
	}
}
