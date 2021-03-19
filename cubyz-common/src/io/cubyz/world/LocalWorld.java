package io.cubyz.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.cubyz.Constants;
import io.cubyz.entity.Player;
import io.cubyz.save.WorldIO;

import static io.cubyz.CubyzLogger.logger;

public class LocalWorld extends World {
	
	private ArrayList<Player> onlinePlayers = new ArrayList<Player>();
	protected boolean generated;
	protected Random rnd;
	protected String name;
	
	private ArrayList<StellarTorus> toruses = new ArrayList<>();
	private LocalSurface currentTorus; // TODO: Support multiple surfaces at the same time.
	private long milliTime;
	private long gameTime;
	public boolean inLqdUpdate;
	private WorldIO wio;
	
	public LocalWorld(String name) {
		this.name = name;
		wio = new WorldIO(this, new File("saves/" + name));
		if (wio.hasWorldData()) {
			wio.loadWorldSeed();
			wio.loadWorldData();
			generated = true;
		} else {
			this.seed = new Random().nextInt();
			wio.saveWorldData();
		}
		rnd = new Random(seed);
		milliTime = System.currentTimeMillis();
	}
	
	public LocalWorld(int seed) {
		this.name = "multiplayer";
		this.seed = seed;
		rnd = new Random(seed);
		milliTime = System.currentTimeMillis();
	}
	
	public void forceSave() {
		if(wio!=null)
			wio.saveWorldData();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public ArrayList<Player> getOnlinePlayers() {
		return onlinePlayers;
	}

	@Override
	public long getGameTime() {
		return gameTime;
	}

	@Override
	public void setGameTime(long time) {
		this.gameTime = time;
	}

	@Override
	public List<StellarTorus> getToruses() {
		return toruses;
	}

	public LocalSurface getCurrentTorus() {
		return currentTorus;
	}
	
	public void setCurrentTorusID(long seed) {
		LocalStellarTorus torus = new LocalStellarTorus(this, seed);
		currentTorus = new LocalSurface(torus, Constants.chunkProvider);
		toruses.add(torus);
	}
	
	// Returns the blocks, so their meshes can be created and stored.
	public void generate() {
		if (!generated) {
			seed = rnd.nextInt();
		}
		Random rand = new Random(seed);
		if (currentTorus == null) {
			LocalStellarTorus torus = new LocalStellarTorus(this, rand.nextLong());
			currentTorus = new LocalSurface(torus, Constants.chunkProvider);
			toruses.add(torus);
		}
		generated = true;
		if(wio!=null)
			wio.saveWorldData();
	}
	
	@Override
	public void cleanup() {
		currentTorus.cleanup();
	}
	
	boolean loggedUpdSkip = false;
	boolean DO_LATE_UPDATES = false;
	public void update() {
		// Time
		if(milliTime + 100 < System.currentTimeMillis()) {
			milliTime += 100;
			inLqdUpdate = true;
			gameTime++; // gameTime is measured in 100ms.
			if ((milliTime + 100) < System.currentTimeMillis()) { // we skipped updates
				if (!loggedUpdSkip) {
					if (DO_LATE_UPDATES) {
						logger.warning(((System.currentTimeMillis() - milliTime) / 100) + " updates late! Doing them.");
					} else {
						logger.warning(((System.currentTimeMillis() - milliTime) / 100) + " updates skipped!");
					}
					loggedUpdSkip = true;
				}
				if (DO_LATE_UPDATES) {
					update();
				} else {
					milliTime = System.currentTimeMillis();
				}
			} else {
				loggedUpdSkip = false;
			}
		}
		
		currentTorus.update();
	}

	@Override
	public Player connectPlayer(UUID playerID) {
		Player player = currentTorus.connectPlayer(playerID);
		onlinePlayers.add(player);
		return player;
	}

	@Override
	public void disconnectPlayer(UUID playerID) {
		currentTorus.disconnectPlayer(playerID);
		for(int i = 0; i < onlinePlayers.size(); i++) {
			if(onlinePlayers.get(i).getPlayerID().equals(playerID)) {
				onlinePlayers.remove(i);
				i--;
			}
		}
	}

}
