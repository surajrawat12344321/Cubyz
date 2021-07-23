package cubyz.utils;

public class CubyzMath {
	/**
	 * Returns the position of the highest set bit starting with log₂(1) = 0.
	 * @param in
	 * @return log₂(in)
	 */
	public static int binaryLog(int in) {
		int log = 0;
		if((in & (0b11111111_11111111_00000000_00000000)) != 0) {
			log += 16;
			in >>= 16;
		}
		if((in & (0b11111111_00000000)) != 0) {
			log += 8;
			in >>= 8;
		}
		if((in & (0b11110000)) != 0) {
			log += 4;
			in >>= 4;
		}
		if((in & (0b1100)) != 0) {
			log += 2;
			in >>= 2;
		}
		if((in & (0b10)) != 0) {
			log += 1;
		}
		return log;
	}
	/**
	 * Returns the position of the highest set bit starting with log₂(1) = 0.
	 * @param in
	 * @return log₂(in)
	 */
	public static int binaryLog(long in) {
		int log = 0;
		if((in & (0b11111111_11111111_11111111_11111111_00000000_00000000_00000000_00000000L)) != 0) {
			log += 16;
			in >>= 16;
		}
		if((in & (0b11111111_11111111_00000000_00000000L)) != 0) {
			log += 16;
			in >>= 16;
		}
		if((in & (0b11111111_00000000L)) != 0) {
			log += 8;
			in >>= 8;
		}
		if((in & (0b11110000L)) != 0) {
			log += 4;
			in >>= 4;
		}
		if((in & (0b1100L)) != 0) {
			log += 2;
			in >>= 2;
		}
		if((in & (0b10L)) != 0) {
			log += 1;
		}
		return log;
	}
	/**
	 * Fills all bits after the leading 1 with 1s.
	 * Example input:  00001010 11000101
	 * Example output: 00001111 11111111
	 * @param input
	 * @return
	 */
	public static long fillBits(long input) {
		int bitLength = binaryLog(input);
		return ~(-2L << bitLength);
	}
}
