package io.cubyz.server;

import io.cubyz.server.modding.ModLoader;
import io.cubyz.utils.gui.StatusInfo;
import io.cubyz.world.Universe;
import io.cubyz.world.UniverseInterface;

public class Server {
	/**
	 * if you're running the server on your own.
	 * 
	 */
	public static UniverseInterface universe;
	public static void main(String[] strings) {
		ModLoader modLoader = new ModLoader(null, new StatusInfo(), new BaseMod());
		universe = new Universe();
	}
}
