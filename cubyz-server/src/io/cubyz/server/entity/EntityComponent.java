package io.cubyz.server.entity;

/**
 * A component allows to add additional functionality to an entity.<br>
 * One example use-case is the AI.
 */
public interface EntityComponent {
	/**
	 * Called once every update.
	 * @param entity the entity this component is associated with.
	 */
	public abstract void update(Entity entity);
}
