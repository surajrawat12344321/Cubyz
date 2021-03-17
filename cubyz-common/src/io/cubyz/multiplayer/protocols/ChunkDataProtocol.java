package io.cubyz.multiplayer.protocols;

import io.cubyz.api.Resource;
import io.cubyz.multiplayer.Connection;
import io.cubyz.multiplayer.Protocol;

public class ChunkDataProtocol extends Protocol{
	
	@Override
	public Protocol generate() {
		return new ChunkDataProtocol();
	}
	
	
	@Override
	public Resource getRegistryID() {
		return new Resource("cubyz:protocol_ChunkData");
	}

	@Override
	public void runClient(Connection conno, boolean initializer) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runServer(Connection conno, boolean initializer) {
		// TODO Auto-generated method stub
		
	}
}
