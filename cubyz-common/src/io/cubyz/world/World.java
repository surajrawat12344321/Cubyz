package io.cubyz.world;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.cubyz.entity.Player;

/**
 * Base class for Cubyz worlds.
 */

public abstract class World {

	protected int seed;
	
	/**
	 * The first player in the list is guaranteed to be the local player, if it exists.
	 * @return a list of all players online on this world.
	 */
	public abstract ArrayList<Player> getOnlinePlayers();

	public abstract void cleanup();
	
	public abstract long getGameTime();
	public abstract void setGameTime(long time);
	
	public boolean isLocal() {
		return this instanceof LocalWorld;
	}
	
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	public int getSeed() {
		return seed;
	}
	
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}
	
	public String getName() {
		throw new UnsupportedOperationException();
	}
	
	public abstract List<StellarTorus> getToruses();
	
	public void update() {}

	public abstract Player connectPlayer(UUID playerID);
	public abstract void disconnectPlayer(UUID playerID);

}
