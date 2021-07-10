package cubyz.world.entity;

import java.util.ArrayList;

import org.joml.Vector3f;

/**
 * super type of all complex entities, such as players and animals.<br>
 * <b>Not</b> included are: item drops, projectiles, block entities, particles.
 */

public class Entity {
	
	private static final Vector3f VECTOR_FOR_CALCULATION_IN_UPDATE_THREAD = new Vector3f();
	
	private Vector3f position = new Vector3f();
	private Vector3f velocity = new Vector3f();
	/**Direction the entity is facing in.*/
	private Vector3f targetDirection = new Vector3f();
	/**Velocity the entity wants to have, in the direction of {@code targetDirection}*/
	private float targetVelocity;
	/**Mass of the entity.*/
	public float weight;
	public float hunger, maxHunger;
	public float health, maxHealth;
	/**Maximum speed attainable by normal motion.*/
	public float maxSpeed;
	
	public ArrayList<EntityComponent> components = new ArrayList<EntityComponent>();
	public ArrayList<Effect> activeEffects = new ArrayList<Effect>();
	private EntityType type;
	/**
	 * Force that the entity can apply on itself or others.
	 * Higher forces allow the entity to reach the target velocity easier and let it deal more damage.
	 */
	private float force;
	
	public Entity(EntityType type) {
		this.type = type;
		hunger = maxHunger = type.maxHunger;
		health = maxHealth = type.maxHealth;
		weight = type.weight;
		maxSpeed = type.maxSpeed;
		force = type.force;
	}
	
	public void update() {
		// Check collision and update positions:
		float friction = collisionDetection();
		position.add(velocity);
		
		// The entity has a target velocity that it tries to reach with all its force.
		// To increase velocity it needs some form of friction on the ground or surrounding medium.
		// But that friction also reduces the entities velocity.
		float applicableAcceleration = Math.min(1, friction)*this.force/weight;
		float frictionAcceleration = friction/weight; // TODO: Multiply an arbitrary factor, so the friction feels reasonable.
		// Apply friction first:
		float oldVelocity = velocity.length();
		if(oldVelocity != 0) {
			float newVelocity = oldVelocity - Math.min(frictionAcceleration, oldVelocity);
			velocity.mul(newVelocity/oldVelocity);
		}
		// Apply entity made acceleration:
		VECTOR_FOR_CALCULATION_IN_UPDATE_THREAD.set(targetDirection).mul(targetVelocity).sub(velocity);
		float velocityDifference = VECTOR_FOR_CALCULATION_IN_UPDATE_THREAD.length();
		if(velocityDifference != 0) {
			float actualAcceleration = Math.min(applicableAcceleration, velocityDifference);
			VECTOR_FOR_CALCULATION_IN_UPDATE_THREAD.mul(actualAcceleration/velocityDifference);
			velocity.add(VECTOR_FOR_CALCULATION_IN_UPDATE_THREAD);
		}
		
		// TODO: hunger and health.
		
	}
	
	/**
	 * Prevents the entity from going inside solid blocks.
	 * Also determines the friction of the surrounding regions.
	 * @return friction
	 */
	public float collisionDetection() {
		// TODO
		return 0;
	}
	
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setTargetDirection(float x, float y, float z) {
		targetDirection.set(x, y, z);
	}
	
	public Vector3f getTargetDirection() {
		return targetDirection;
	}
	
	public void setTargetVelocity(float vel) {
		targetVelocity = Math.min(vel, maxSpeed);
	}
	
	public void removeEffect(Effect effect) {
		if(!effect.isActive()) return;
		for(int i = 0; i < activeEffects.size(); i++) {
			if(effect == activeEffects.get(i)) {
				activeEffects.remove(i--);
			}
		}
		effect.removeEffectFromEntitiy();
		// Deactivate the effect, so it doesn't do anything in the future:
		effect.deactivate();
	}
	
	public void addEffect(Effect effect) {
		// Check if the effect is already present and merge it:
		for(Effect other : activeEffects) {
			if(effect.testEqualityAndMerge(other)) {
				return;
			}
		}
		activeEffects.add(effect);
		if(effect.time != Long.MAX_VALUE) {
			Effects.addEffect(effect);
		}
	}
}
