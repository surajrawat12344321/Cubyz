package cubyz.client.renderUniverse;

import cubyz.world.UniverseInterface;

/**
 * 
 * Renders the Universe
 *
 */
public abstract class RenderUniverse {
	public static UniverseInterface universe;

	//data comes from Game.connection
	public static void draw() {
		if(universe==null)
			return;
		RenderPlanet.render();
	}
}
