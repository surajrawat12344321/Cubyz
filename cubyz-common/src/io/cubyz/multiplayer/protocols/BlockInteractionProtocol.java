package io.cubyz.multiplayer.protocols;

import org.joml.Vector3i;

import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.math.Bits;
import io.cubyz.multiplayer.Connection;
import io.cubyz.multiplayer.Protocol;

/**  Tells the other side,that an individual block has been broken or created.
 * */


public class BlockInteractionProtocol extends Protocol {

	public Vector3i position;
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

		byte data[] = new byte[16];
		Bits.putInt(data, 0, position.x);
		Bits.putInt(data, 4, position.y);
		Bits.putInt(data, 8, position.z);
		Bits.putInt(data, 12, placed.ID);
		
		conno.send(data);
		
	}

	@Override
	public void runServer(Connection conno, boolean initializer) {
		byte data[] = new byte[16];
		data = conno.receive();
		position.x = Bits.getInt(data, 0);
		position.y = Bits.getInt(data, 4);
		position.z = Bits.getInt(data, 8);
		placed.ID = Bits.getInt(data, 12);
		
	}

}
