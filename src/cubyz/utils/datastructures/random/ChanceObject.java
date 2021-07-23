package cubyz.utils.datastructures.random;

/**
 * An object that has a relative chance of being drawn from a list.
 * An example of this are biomes. There are more and less rare biomes, but the rarity isn't global, but always relative to that of all other biomes, because only one can be chosen.
 */

public interface ChanceObject {
	/** The chance is stored as an integer to avoid calculation errors on addition/subtraction. */
	public int chance();
}
