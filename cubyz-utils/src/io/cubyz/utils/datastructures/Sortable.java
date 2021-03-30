package io.cubyz.utils.datastructures;

public interface Sortable<T> {
	/**
	 * Returns true if {@code this} should be sorted before {@code other}
	 * @param other
	 * @return
	 */
	public abstract boolean compare(T other);
}
