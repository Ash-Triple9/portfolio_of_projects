//AUTHOR: Ashiqul Alam
public class Sim2_HalfAdder {

    public RussWire a, b;

    public RussWire sum, carry;

    private XOR xorGate;
    private AND andGate;

    public Sim2_HalfAdder() {

        a = new RussWire();
        b = new RussWire();
        sum = new RussWire();
        carry = new RussWire();

        xorGate = new XOR();
        andGate = new AND();
    }

    public void execute() {

        xorGate.a.set(a.get());
        xorGate.b.set(b.get());
        xorGate.execute();
        sum.set(xorGate.out.get());

        andGate.a.set(a.get());
        andGate.b.set(b.get());
        andGate.execute();
        carry.set(andGate.out.get());
    }
}
