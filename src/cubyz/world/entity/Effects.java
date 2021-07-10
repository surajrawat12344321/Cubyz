package cubyz.world.entity;

import cubyz.utils.datastructures.SortedList;

/**
 * Stores all limited time effects of the game and is responsible for removing them when the time is up.
 */
class Effects {
	private static SortedList<Effect> effects = new SortedList<Effect>(new Effect[10]);
	
	static void addEffect(Effect effect) {
		effects.insert(effect);
	}
	
	/**
	 * Checks if any effects have run out and disables them.
	 */
	public static void update() {
		long time = System.currentTimeMillis();
		while(effects.size() != 0 && effects.getLast().time - time <= 0) {
			Effect toRemove = effects.getLast();
			effects.removeLast();
			toRemove.entity.removeEffect(toRemove);
		}
	}
}
