package io.cubyz.server.entity;

import io.cubyz.utils.datastructures.RegistryElement;

/**
 * 
 */

public abstract class EntityType implements RegistryElement {
	/**
	 * Health and hunger on creation. 
	 */
	public final float maxHealth, maxHunger;
	/** Maximum speed attainable by normal motion. */
	public final float maxSpeed;
	/**
	 * Force that the entity can apply on itself or others.
	 * Higher forces allow the entity to reach the target velocity easier and let it deal more damage.
	 */
	public final float force;
	/**
	 * Mass of the entity. Used to calculate acceleration using Newton's law {@code F = ma}.
	 */
	public final float weight;
	
	/**
	 * Initializes the base
	 * @param health max health of the entity without any effects.
	 * @param hunger max hunger of the entity without any effects.
	 * @param speed max speed of the entity without any effects.
	 * @param force movement force and attack force without any effects.
	 * @param weight
	 */
	public EntityType(float health, float hunger, float speed, float force, float weight) {
		maxHealth = health;
		maxHunger = hunger;
		maxSpeed = speed;
		this.force = force;
		this.weight = weight;
	}
}
