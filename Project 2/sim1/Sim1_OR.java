/* Simulates a physical OR gate.
 *
 * Author: Ashiqul Alam
 */

public class Sim1_OR {
	public void execute() {
		// TODO: fill this in!
		out.set(a.get() || b.get());
	}

	public RussWire a, b; // inputs
	public RussWire out; // output

	public Sim1_OR() {
		// TODO: fill this in!
		a = new RussWire();
		b = new RussWire();
		out = new RussWire();
	}
}
