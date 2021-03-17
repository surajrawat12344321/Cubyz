package io.cubyz.entity.models;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import io.cubyz.api.Resource;
import io.cubyz.entity.Entity;
import io.cubyz.entity.EntityModel;
import io.cubyz.entity.EntityType;
import io.cubyz.entity.Player;

/**
 * An entity model for all possible quadruped mobs that handles model creation and movement animation.<br>
 * TODO: Simplify this and allow for custom head/leg/body models.
 */

public class Quadruped implements EntityModel {
	protected enum MovementPattern {
		STABLE, FAST,
	};
	// Registry stuff:
	Resource id = new Resource("cuybz:quadruped");
	public Quadruped() {}
	@Override
	public Resource getRegistryID() {
		return id;
	}
	@Override
	public EntityModel createInstance(String data, EntityType source) {
		
		return new Quadruped(data, source);
	}
	
	// Actual model stuff:
	public float bodyWidth, bodyLength, bodyHeight, legWidth, legHeight, headWidth, headLength, headHeight;
	public MovementPattern movementPattern;
	
	public Quadruped(String data, EntityType source) {
		// Parse data:
		String[] lines = data.split("\n");
		for(String line : lines) {
			String[] parts = line.replaceAll("\\s", "").split(":");
			if(parts[0].equals("body")) {
				String[] arguments = parts[1].split("x");
				bodyWidth = Integer.parseInt(arguments[0])/16.0f;
				bodyLength = Integer.parseInt(arguments[1])/16.0f;
				bodyHeight = Integer.parseInt(arguments[2])/16.0f;
			} else if(parts[0].equals("head")) {
				String[] arguments = parts[1].split("x");
				headWidth = Integer.parseInt(arguments[0])/16.0f;
				headLength = Integer.parseInt(arguments[1])/16.0f;
				headHeight = Integer.parseInt(arguments[2])/16.0f;
			} else if(parts[0].equals("leg")) {
				String[] arguments = parts[1].split("x");
				legWidth = Integer.parseInt(arguments[0])/16.0f;
				legHeight = Integer.parseInt(arguments[1])/16.0f;
			} else if(parts[0].equals("movement")) {
				movementPattern = MovementPattern.valueOf(parts[1].toUpperCase());
			}
		}
	}
	@Override
	public void update(Entity ent) {
		float v = (float)Math.sqrt(ent.vx*ent.vx + ent.vz*ent.vz);
		ent.movementAnimation += v;
		ent.movementAnimation %= 2*legHeight;
	}
	@Override
	public float getCollisionDistance(Vector3f playerPosition, Vector3f dir, Entity ent) {
		float xNorm = ent.targetVX/(float)Math.sqrt(ent.targetVX*ent.targetVX + ent.targetVZ*ent.targetVZ);
		float zNorm = ent.targetVZ/(float)Math.sqrt(ent.targetVX*ent.targetVX + ent.targetVZ*ent.targetVZ);
		Vector3f newDir = new Vector3f(dir);
		newDir.z = dir.x*xNorm + dir.z*zNorm;
		newDir.x = -dir.x*zNorm + dir.z*xNorm;
		float distanceZ = (ent.getPosition().x-playerPosition.x)*xNorm + (ent.getPosition().z-playerPosition.z)*zNorm;
		float distanceX = -(ent.getPosition().x-playerPosition.x)*zNorm + (ent.getPosition().z-playerPosition.z)*xNorm;
		Vector2f res = new Vector2f();
		boolean intersects = Intersectionf.intersectRayAab(0, playerPosition.y+Player.cameraHeight, 0, newDir.x, newDir.y, newDir.z, distanceX-bodyWidth/2, ent.getPosition().y, distanceZ-bodyLength/2, distanceX+bodyWidth/2, ent.getPosition().y+bodyHeight+legHeight, distanceZ+bodyLength/2+headLength-0.01f, res);
		return intersects ? res.x : Float.MAX_VALUE;
	}
	
}
