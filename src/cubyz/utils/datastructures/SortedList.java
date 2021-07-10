package cubyz.utils.datastructures;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A list that is sorted and allows sorted insertion and removal on the back.
 *
 * @param <T extends Sortable<T>>
 */

public class SortedList<T extends Sortable<T>> {
	
	private T[] array;
	private int size;
	
	/**
	 * Creates a new array with the given array as initialcapacity
	 * @param initialArray != null
	 */
	public SortedList(T[] initialArray) {
		array = initialArray;
		size = 0;
		if(array.length < 2) {
			increaseCapacity(2); // If it's lower than 2 the resizing won't work.
		}
	}
	
	private void increaseCapacity(int newCapacity) {
		array = Arrays.copyOf(array, newCapacity);
	}
	
	/**
	 * insertion sort the object into the queue.
	 */
	public synchronized void insert(T object) {
		if(size == array.length) increaseCapacity(array.length*3/2);
		int i = 0;
		for(; i < size; i++) {
			if(object.compare(array[i])) {
				break;
			}
		}
		// Shift all elements that are bigger:
		if(i < size) {
			System.arraycopy(array, i, array, i+1, size - i);
		}
		// Insert the new element:
		array[i] = object;
		size++;
	}
	
	public void removeLast() {
		array[--size] = null;
	}
	
	public T getLast() {
		return array[size - 1];
	}
	
	public void foreach(Consumer<T> consumer) {
		for(int i = 0; i < size; i++) {
			consumer.accept(array[i]);
		}
	}
	
	public int size() {
		return size;
	}
}
