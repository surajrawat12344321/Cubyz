package io.cubyz.blocks.rotation;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.blocks.RotationMode;
import io.cubyz.entity.Entity;
import io.cubyz.util.ByteWrapper;
import io.cubyz.world.NormalChunk;
import io.cubyz.world.Surface;

public class FenceRotation implements RotationMode {
	Resource id = new Resource("cubyz", "fence");
	@Override
	public Resource getRegistryID() {
		return id;
	}

	@Override
	public boolean generateData(Surface surface, int x, int y, int z, Vector3f relativePlayerPosition, Vector3f playerDirection, Vector3i relativeDirection, ByteWrapper currentData, boolean blockPlacing) {
		if(!blockPlacing) return false;
		NormalChunk chunk = surface.getChunk(x >> NormalChunk.chunkShift, y >> NormalChunk.chunkShift, z >> NormalChunk.chunkShift);
		currentData.data = (byte)1;
		// Get all neighbors and set the corresponding bits:
		Block[] neighbors = chunk.getNeighbors(x, y ,z);
		if(neighbors[0] != null && neighbors[0].isSolid()) {
			currentData.data |= 0b00010;
		}
		if(neighbors[1] != null && neighbors[1].isSolid()) {
			currentData.data |= 0b00100;
		}
		if(neighbors[2] != null && neighbors[2].isSolid()) {
			currentData.data |= 0b01000;
		}
		if(neighbors[3] != null && neighbors[3].isSolid()) {
			currentData.data |= 0b10000;
		}
		return true;
	}

	@Override
	public boolean dependsOnNeightbors() {
		return true;
	}

	@Override
	public Byte updateData(byte data, int dir, Block newNeighbor) {
		if(dir == 4 | dir == 5) return data;
		byte mask = (byte)(1 << (dir + 1));
		data &= ~mask;
		if(newNeighbor != null && newNeighbor.isSolid())
			data |= mask;
		return data;
	}

	@Override
	public boolean checkTransparency(byte data, int dir) {
		return true;
	}

	@Override
	public byte getNaturalStandard() {
		return 1;
	}

	@Override
	public boolean changesHitbox() {
		return true;
	}

	@Override
	public boolean checkEntity(Vector3f pos, float width, float height, int x, int y, int z, byte blockData) {
		// Hit area is just a simple + with a width of 0.25:
		return y >= pos.y
				&& y <= pos.y + height
				&&
				(
					( // - of the +:
						x + 0.625f >= pos.x - width
						&& x + 0.375f <= pos.x+ width
						&& z + 1 >= pos.x - width
						&& z <= pos.x + width
					)
					||
					( // | of the +:
						z + 0.625f >= pos.z - width
						&& z + 0.375f <= pos.z+ width
						&& x + 1 >= pos.x - width
						&& x <= pos.x + width
					)
				);
	}

	@Override
	public boolean checkEntityAndDoCollision(Entity ent, Vector4f vel, int x, int y, int z, byte blockData) {
		// Hit area is just a simple + with a width of 0.25:
		float xOffset = 0;
		float xLen = 1;
		float zOffset = 0;
		float zLen = 1;
		if((blockData & 0b00010) == 0) {
			xOffset += 0.5f;
			xLen -= 0.5f;
		}
		if((blockData & 0b00100) == 0) {
			xLen -= 0.5f;
		}
		if((blockData & 0b01000) == 0) {
			zOffset += 0.5f;
			zLen -= 0.5f;
		}
		if((blockData & 0b10000) == 0) {
			zLen -= 0.5f;
		}
		
		ent.aabCollision(vel, x + xOffset, y, z + 0.375f, xLen, 1, 0.25f, blockData);
		ent.aabCollision(vel, x + 0.375f, y, z + zOffset, 0.35f, 1, zLen, blockData);
		return false;
	}
}
