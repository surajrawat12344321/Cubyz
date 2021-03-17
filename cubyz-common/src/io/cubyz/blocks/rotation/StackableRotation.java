package io.cubyz.blocks.rotation;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.blocks.RotationMode;
import io.cubyz.entity.Entity;
import io.cubyz.util.ByteWrapper;
import io.cubyz.world.Surface;

/**
 * For stackable partial blocks, like snow.
 */

public class StackableRotation implements RotationMode {
	
	Resource id = new Resource("cubyz", "stackable");
	@Override
	public Resource getRegistryID() {
		return id;
	}

	@Override
	public boolean generateData(Surface surface, int x, int y, int z, Vector3f relativePlayerPosition, Vector3f playerDirection, Vector3i relativeDirection, ByteWrapper currentData, boolean blockPlacing) {
		if(blockPlacing) {
			currentData.data = 1;
			return true;
		}
		Vector3f min = new Vector3f();
		Vector3f max = new Vector3f(1, currentData.data/16.0f, 1);
		Vector2f result = new Vector2f();
		// Check if the ray is going through the block:
		if(Intersectionf.intersectRayAab(relativePlayerPosition, playerDirection, min, max, result)) {
			// Check if the ray is going through the top layer and going the right direction:
			min.y = max.y - 0.0001f;
			if(playerDirection.y < 0 && Intersectionf.intersectRayAab(relativePlayerPosition, playerDirection, min, max, result)) {
				if(currentData.data == 16) return false;
				currentData.data++;
				return true;
			}
			return false;
		} else {
			if(currentData.data == 16) return false;
			currentData.data++;
			return true;
		}
	}

	@Override
	public boolean dependsOnNeightbors() {
		return false;
	}

	@Override
	public Byte updateData(byte data, int dir, Block newNeighbor) {
		return data;
	}

	@Override
	public boolean checkTransparency(byte data, int dir) {
		if(data < 16) {//TODO: && ((dir & 1) != 0 || (dir & 512) == 0)) {
			return true;
		}
		return false;
	}

	@Override
	public byte getNaturalStandard() {
		return 16;
	}

	@Override
	public boolean changesHitbox() {
		return true;
	}

	@Override
	public boolean checkEntity(Vector3f pos, float width, float height, int x, int y, int z, byte blockData) {
		return 	   y + blockData/16.0f >= pos.y
				&& y     <= pos.y + height
				&& x + 1 >= pos.x - width
				&& x     <= pos.x + width
				&& z + 1 >= pos.z - width
				&& z     <= pos.z + width;
	}

	@Override
	public boolean checkEntityAndDoCollision(Entity ent, Vector4f vel, int x, int y, int z, byte data) {
		// Check if the player can step onto this:
		if(y + data/16.0f - ent.getPosition().y > 0 && y + data/16.0f - ent.getPosition().y <= ent.stepHeight) {
			vel.w = Math.max(vel.w, y + data/16.0f - ent.getPosition().y);
			return false;
		}
		if(vel.y == 0) {
			return	   y + data/16.0f >= ent.getPosition().y
					&& y <= ent.getPosition().y + ent.height;
		}
		if(vel.y >= 0) {
			return true;
		}
		if(y + data/16.0f >= ent.getPosition().y + vel.y) {
			vel.y = y + data/16.0f + 0.01f - ent.getPosition().y;
		}
		return false;
	}
}
