/* Simulates a physical device that performs (signed) subtraction on
 * a 32-bit input.
 *
 * Author: Ashiqul Alam
 */

public class Sim1_SUB {
	public void execute() {
		// TODO: fill this in!
		//
		// REMEMBER: You may call execute() on sub-objects here, and
		// copy values around - but you MUST NOT create
		// objects while inside this function.

		// execute 2s complement on bBits
		for (int i = 0; i < 32; i++) {
			do2sComplement.in[i].set((b[i].get()));
		}
		do2sComplement.execute();

		// add aBits to bBits
		addItUp.a = a;
		addItUp.b = do2sComplement.out;
		addItUp.execute();

		// send the output to sum
		for (int i = 0; i < 32; i++) {
			sum[i].set(addItUp.sum[i].get());
		}

	}

	// --------------------
	// Don't change the following standard variables...
	// --------------------

	// inputs
	public RussWire[] a, b;

	// output
	public RussWire[] sum;

	// --------------------
	// But you should add some *MORE* variables here.
	// --------------------
	// TODO: fill this in
	public Sim1_2sComplement do2sComplement;
	public Sim1_ADD addItUp;

	public Sim1_SUB() {
		// TODO: fill this in!
		a = new RussWire[32];
		b = new RussWire[32];
		sum = new RussWire[32];

		do2sComplement = new Sim1_2sComplement();
		addItUp = new Sim1_ADD();
		for (int i = 0; i < 32; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			sum[i] = new RussWire();
		}
	}
}
