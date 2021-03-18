package io.cubyz.multiplayer.protocols;

import org.joml.Vector3f;

import io.cubyz.Constants;
import io.cubyz.api.Resource;
import io.cubyz.math.Bits;
import io.cubyz.multiplayer.Connection;
import io.cubyz.multiplayer.Protocol;
import io.cubyz.world.LocalWorld;

public class InitProtocol extends Protocol {

	public Vector3f position;
	
	
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
		byte[] data = conno.receive();
		
		int seed = Bits.getInt(data,0);
		
		position = new Vector3f();
		
		position.x = Bits.getFloat(data,4);
		position.y = Bits.getFloat(data,8);
		position.z = Bits.getFloat(data,12);
		
		Constants.world = new LocalWorld(seed);
		
		//Constants.world.generate();
	}

	@Override
	public void runServer(Connection conno, boolean initializer) {
		//send the universe :D
		byte data[] = new byte[16];
		// TODO: Create a new player using the UUID.
		Vector3f position = Constants.world.getOnlinePlayers().get(0).getPosition();
		
		Bits.putInt(data, 0, Constants.world.getSeed());
		Bits.putFloat(data,4,position.x);
		Bits.putFloat(data,8,position.y);
		Bits.putFloat(data,12,position.z);
		
		System.out.println("x:"+position.x);
		System.out.println("y:"+position.y);
		System.out.println("z:"+position.z);
		
		
		conno.send(data);
		
		
	}

}
