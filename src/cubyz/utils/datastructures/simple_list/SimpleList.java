package cubyz.utils.datastructures.simple_list;

import java.util.Arrays;

/**
 * A simple list that mostly supports adding elements.
 * Unlike regular lists it also offers access to the underlying data for easier processing.
 */

public class SimpleList<T> {
	public int size;
	public T[] array;
	/**
	 * @param initialCapacity
	 */
	public SimpleList(int initialCapacity) {
		array = (T[])new Object[initialCapacity];
	}
	public SimpleList() {
		this(10);
	}
	/**
	 * Adds one element.
	 * @param t
	 */
	public void add(T t) {
		if(array.length == size) {
			increaseCapacity(array.length*2);
		}
		array[size++] = t;
	}
	/**
	 * Adds an array of elements.
	 * @param t
	 */
	public void add(T[] t) {
		if(array.length <= size + t.length) {
			increaseCapacity(array.length*2 + t.length);
		}
		System.arraycopy(t, 0, array, size, t.length);
		size += t.length;
	}
	/**
	 * Adds a part of an array specified by it's start and length
	 * @param t
	 * @param start
	 * @param length
	 */
	public void add(T[] t, int start, int length) {
		// Some bound checks:
		if(start < 0)
			start = 0;
		if(length + start > t.length)
			length = t.length - start;
		if(length <= 0)
			return;
		
		
		if(array.length <= size + length) {
			increaseCapacity(array.length*2 + length);
		}
		System.arraycopy(t, start, array, size, length);
		size += length;
	}
	/**
	 * Removes all elements inside. WARNING: This does not delete references, so beware of memory leaks!
	 */
	public void clear() {
		size = 0;
	}
	
	void increaseCapacity(int newCapacity) {
		array = Arrays.copyOf(array, newCapacity);
	}
}
