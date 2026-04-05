/* Simulates a physical device that performs 2's complement on a 32-bit input.
 *
 * Author: Ashiqul Alam
 */

public class Sim1_2sComplement {
	public void execute() {
		// TODO: fill this in!
		//
		// REMEMBER: You may call execute() on sub-objects here, and
		// copy values around - but you MUST NOT create
		// objects while inside this function.

		// negate the value
		for (int i = 0; i < 32; i++) {
			not_a[i].in.set(in[i].get());
			not_a[i].execute();
		}

		// add one to the negated value
		for (int i = 0; i < 32; i++) {
			if (i == 0) {
				addOne.a[i].set(true);
			} else {
				addOne.a[i].set(false);
			}
			addOne.b[i].set(not_a[i].out.get());
		}
		addOne.execute();

		// set the output to out
		for (int i = 0; i < 32; i++) {
			out[i].set(addOne.sum[i].get());
		}
	}

	// you shouldn't change these standard variables...
	public RussWire[] in;
	public RussWire[] out;

	// TODO: add some more variables here. You must create them
	// during the constructor below. REMEMBER: You're not
	// allowed to create any object inside the execute()
	// method above!
	public Sim1_NOT[] not_a;
	public Sim1_ADD addOne;

	public Sim1_2sComplement() {
		// TODO: this is where you create the objects that
		// you declared up above.
		not_a = new Sim1_NOT[32];
		addOne = new Sim1_ADD();

		in = new RussWire[32];
		out = new RussWire[32];

		for (int i = 0; i < 32; i++) {
			not_a[i] = new Sim1_NOT();
			in[i] = new RussWire();
			out[i] = new RussWire();
		}

	}
}
