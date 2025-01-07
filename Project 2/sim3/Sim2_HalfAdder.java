//AUTHOR: Ashiqul Alam
public class Sim2_HalfAdder {

    public RussWire a, b;

    public RussWire sum, carry;

    private XOR xorGate;
    private AND andGate;

    public Sim2_HalfAdder() {

        // initialize the variables and the logic gates
        a = new RussWire();
        b = new RussWire();
        sum = new RussWire();
        carry = new RussWire();

        xorGate = new XOR();
        andGate = new AND();
    }

    public void execute() {

        // Use XOR gates to compute the logic between a and b
        xorGate.a.set(a.get());
        xorGate.b.set(b.get());
        xorGate.execute();
        sum.set(xorGate.out.get());

        // Use an AND gate to get the carry value
        andGate.a.set(a.get());
        andGate.b.set(b.get());
        andGate.execute();
        carry.set(andGate.out.get());
    }
}

