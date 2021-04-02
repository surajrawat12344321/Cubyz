package io.cubyz.world.entity;

import io.cubyz.utils.datastructures.RegistryElement;
import io.cubyz.utils.datastructures.Sortable;

/**
 * Similar to entity component, but has no update functionality and gets removed after a certain time.<br>
 * Two effects of the same type get combined into one with longer effect time.
 */
public abstract class Effect implements Sortable<Effect>, RegistryElement {
	/**
	 * Time the effect runs out. Given in system time.
	 */
	public long time;
	public final Entity entity;
	private boolean active = true;
	/**
	 * Constructs a new effect with duration.
	 * @param entity must not be null.
	 * @param duration time the effect should last in milliseconds. -1 if effect duration is uncertain or infinite.
	 */
	public Effect(Entity entity, int duration) {
		this.entity = entity;
		if(duration >= 0) {
			time = System.currentTimeMillis() + duration;
		} else {
			time = Long.MAX_VALUE;
		}
	}
	
	/**
	 * Removes {@code this} effect from the entity that has it.
	 */
	public abstract void removeEffectFromEntitiy();
	
	/**
	 * Tests if the two effects are equal and merges them into {@code other} if that's the case.
	 * @param other
	 * @return if they are equal.
	 */
	public abstract boolean testEqualityAndMerge(Effect other);
	
	public boolean compare(Effect other) {
		return this.time < other.time;
	}
	
	public void deactivate() {
		active = false;
	}
	
	public boolean isActive() {
		return active;
	}
}
