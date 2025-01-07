import java.util.Arrays;

/**
 * Test that the mux outputs the correct values from each of 8 possible input combinations
 * @author Andrew Dennison
 */
public class Test_AD_MUX {
	
	public static void main(String[] args) {
		// Test the mux is outputting the correct value
		for (int i = 0; i < 8; i++) {
			testMux(new Sim3_MUX_8by1(), i);
		}
	}
	
	/**
	 * Given a MUX, and a positional parameter, ensure that the MUX properly returns the boolean
	 * value contained at 'position' within an input.  In this case, the value at 'position' is
	 * true, while all other values are false.
	 * @param mux			<Sim3_MUX_8by1> Implementation of a MUX 
	 * @param testOutput	<Integer> Positional parameter
	 */
	public static void testMux(Sim3_MUX_8by1 mux, int position) {
		
		// Create an input array of all false values, with a true at the test output
		boolean[] muxInput = new boolean[8];
		muxInput[position] = true;
		
		// Convert our given integer to a binary array of boolean values
		boolean[] controlInput = decToBin(position);
		
		for (int i = 0; i < muxInput.length; i++) {
			if (i < 3) mux.control[i].set(controlInput[i]);
			mux.in[i].set(muxInput[i]);
		}
		
		mux.execute();
		
		System.out.printf("MUX: control=%d in=%s -> %b\n", position, Arrays.toString(muxInput), mux.out.get());
	}
	
	/**
	 * Convert a given number < 8 to a boolean array of length 3.  Could be adjusted for higher numbers
	 * given the necessary length is calculated first, instead of assuming 3.
	 * @param decimal	<Integer> Int representation of the number to be converted to binary
	 * @return			<Boolean><Array> Boolean[3] where bin[0] = LSB and bin[2] = MSB
	 */
	public static boolean[] decToBin(int decimal) {		
		boolean[] bin = new boolean[3];
		
		// Repeatedly divide by powers of 2
		for (int i = bin.length - 1; i >= 0; i--) {
			int currPow = (int) Math.pow(2, i);
			if (currPow <= decimal) {
				decimal -= currPow;
				bin[i] = true;
			}
		}
		
		return bin;
	}
}
