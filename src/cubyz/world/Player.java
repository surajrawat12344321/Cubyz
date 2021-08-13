package cubyz.world;

import java.util.UUID;

import cubyz.world.entity.Entity;

public class Player {
	public UUID uuid;
	public Entity entityInWorld;
	public Player(UUID uuid) {
		this.uuid = uuid;
	}
}
