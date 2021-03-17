package io.cubyz.multiplayer.protocols;

import io.cubyz.Constants;
import io.cubyz.api.Resource;
import io.cubyz.math.Bits;
import io.cubyz.multiplayer.Connection;
import io.cubyz.multiplayer.Protocol;
import io.cubyz.world.LocalWorld;

public class InitProtocol extends Protocol {

	@Override
	public Protocol generate() {
		return new InitProtocol();
	}
	
	@Override
	public Resource getRegistryID() {
		return new Resource("cubyz:protocol_init");
	}

	@Override
	public void runClient(Connection conno, boolean initializer) {
		//receive the universe :D
		int seed = Bits.getInt(conno.receive(),0);
		Constants.world = new LocalWorld(seed);
		Constants.world.generate();
	}

	@Override
	public void runServer(Connection conno, boolean initializer) {
		//sendthe universe :D
		byte seed[] = new byte[4];
		Bits.putInt(seed, 0, Constants.world.getSeed());
		conno.send(seed);
		
		
	}

}
