package cubyz.utils.datastructures.random;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import cubyz.utils.CubyzMath;

/**
 * A list that allows to choose randomly from the contained object, if they have a chance assigned to them.
 * @param <T>
 */

public class RandomList<T extends ChanceObject> {
	private static final int arrayIncrease = 16;
	
	private T[] array;
	private int size;
	private long sum;
	
	public RandomList(T[] initial) {
		size = 0;
		sum = 0;
		this.array = initial;
	}
	
	private void increaseSize(int increment) {
		array = Arrays.copyOf(array, array.length + increment);
	}
	
	public int size() {
		return size;
	}
	
	public void add(T object) {
		if (size == array.length)
			increaseSize(arrayIncrease);
		array[size] = object;
		size++;
		sum += object.chance();
	}
	
	/**
	 * Iterates through all elements in the list.
	 * @param action
	 */
	public void forEach(Consumer<T> action) {
		for(int i = 0; i < size; i++) {
			action.accept(array[i]);
		}
	}
	
	public T getRandomly(Random rand) {
		long value = rangedRandomLong(rand, sum);
		for(int i = 0; i < size; i++) {
			if(value < array[i].chance())
				return array[i];
			value -= array[i].chance();
		}
		throw new IllegalStateException("Seems like someone made changes to the code without thinking. Report this immediately!");
	}
	
	/**
	 * Calculates a random number between 0 and max.
	 * @param rand
	 * @param max
	 * @return
	 */
	private static long rangedRandomLong(Random rand, long max) {
		long and = CubyzMath.fillBits(max);
		long out = 0;
		do {
			out = rand.nextLong();
			out &= and;
			System.out.println(out+" "+max);
		} while(out >= max);
		return out;
	}
}
