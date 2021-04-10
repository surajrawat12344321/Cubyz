package io.cubyz.server;

import io.cubyz.world.Universe;
import io.cubyz.world.UniverseInterface;

public class Server {
	/**
	 * if you're running the server on your own.
	 * 
	 */
	public static UniverseInterface universe;
	public static void main(String[] strings) {
		universe = new Universe();
	}
}
