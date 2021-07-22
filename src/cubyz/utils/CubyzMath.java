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
}
