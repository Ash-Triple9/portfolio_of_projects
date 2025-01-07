/* Simulates a physical device that performs (signed) addition on
 * a 32-bit input.
 *
 * Author: Ashiqul Alam
 */

public class Sim1_ADD {
	public void execute() {
		boolean carryVal = false;
		// TODO: fill this in!
		for (int i = 0; i < 32; i++) {
			RussWire aBit = a[i];
			RussWire bBit = b[i];

			// make the choice
			if (aBit.get() == false && bBit.get() == false && carryVal == false) {
				sum[i].set(false);
				carryVal = false;
			} else if (aBit.get() == false && bBit.get() == false && carryVal == true) {
				sum[i].set(true);
				carryVal = false;
			} else if (aBit.get() == false && bBit.get() == true && carryVal == false) {
				sum[i].set(true);
				carryVal = false;
			} else if (aBit.get() == false && bBit.get() == true && carryVal == true) {
				sum[i].set(false);
				carryVal = true;
			} else if (aBit.get() == true && bBit.get() == false && carryVal == false) {
				sum[i].set(true);
				carryVal = false;
			} else if (aBit.get() == true && bBit.get() == false && carryVal == true) {
				sum[i].set(false);
				carryVal = true;
			} else if (aBit.get() == true && bBit.get() == true && carryVal == false) {
				sum[i].set(false);
				carryVal = true;
			} else if (aBit.get() == true && bBit.get() == true && carryVal == true) {
				sum[i].set(true);
				carryVal = true;
			}

		}

		// Check for overflow
		boolean aSign = a[31].get();
		boolean bSign = b[31].get();
		boolean sumSign = sum[31].get();
		overflow.set((aSign == bSign) && (sumSign != aSign));

		// set carryOut
		carryOut.set(carryVal);
	}

	// ------
	// It should not be necessary to change anything below this line,
	// although I'm not making a formal requirement that you cannot.
	// ------

	// inputs
	public RussWire[] a, b;

	// outputs
	public RussWire[] sum;
	public RussWire carryOut, overflow;

	public Sim1_ADD() {
		/*
		 * Instructor's Note:
		 *
		 * In Java, to allocate an array of objects, you need two
		 * steps: you first allocate the array (which is full of null
		 * references), and then a loop which allocates a whole bunch
		 * of individual objects (one at a time), and stores those
		 * objects into the slots of the array.
		 */

		a = new RussWire[32];
		b = new RussWire[32];
		sum = new RussWire[32];

		for (int i = 0; i < 32; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			sum[i] = new RussWire();
		}

		carryOut = new RussWire();
		overflow = new RussWire();
	}
}
