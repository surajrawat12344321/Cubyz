package io.cubyz.multiplayer.protocols;

import org.joml.Vector3i;

import io.cubyz.Constants;
import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.math.Bits;
import io.cubyz.multiplayer.Connection;
import io.cubyz.multiplayer.Protocol;

/**  Tells the other side,that an individual block has been broken or created.
 * */


public class BlockInteractionProtocol extends Protocol {

	public Vector3i position = new Vector3i();
	public Block 	placed;
	
	@Override
	public Protocol generate() {
		return new BlockInteractionProtocol();
	}
	
	@Override
	public Resource getRegistryID() {
		return new Resource("cubyz:protocol_blockinteraction");
	}

	@Override
	public void runClient(Connection conno, boolean initializer) {
		
		byte data[] = new byte[12];
		Bits.putInt(data, 0, position.x);
		Bits.putInt(data, 4, position.y);
		Bits.putInt(data, 8, position.z);
		
		
		
		conno.send(data);
		System.out.println(placed.getRegistryID().toString());
		conno.send(placed.getRegistryID().toString().getBytes());
		
	}

	@Override
	public void runServer(Connection conno, boolean initializer) {
		byte data[] = new byte[12];
		data = conno.receive();
		position.x = Bits.getInt(data, 0);
		position.y = Bits.getInt(data, 4);
		position.z = Bits.getInt(data, 8);
		placed =  CubyzRegistries.BLOCK_REGISTRY.getByID(new String(conno.receive()));
		

		Constants.world.currentTorus.placeBlock(position.x, position.y, position.z, placed,(byte) 0);
		
	}

}
