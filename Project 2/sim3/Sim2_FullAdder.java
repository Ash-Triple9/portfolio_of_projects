//Author: Ashiqul Alam
public class Sim2_FullAdder {

    public RussWire a, b, carryIn;

    public RussWire sum, carryOut;

    private Sim2_HalfAdder halfAdder1, halfAdder2;
    private OR orGate;

    public Sim2_FullAdder() {

        // Initialize the input and halfadder to be used
        a = new RussWire();
        b = new RussWire();
        carryIn = new RussWire();
        sum = new RussWire();
        carryOut = new RussWire();

        halfAdder1 = new Sim2_HalfAdder();
        halfAdder2 = new Sim2_HalfAdder();
        orGate = new OR();
    }

    public void execute() {

        // feed the values of a and b to one of the halfadder
        halfAdder1.a.set(a.get());
        halfAdder1.b.set(b.get());
        halfAdder1.execute();

        // use the carryIn as well as the sum from the first halfadder
        // to  the other halfadder
        halfAdder2.a.set(halfAdder1.sum.get());
        halfAdder2.b.set(carryIn.get());
        halfAdder2.execute();

        sum.set(halfAdder2.sum.get());

        // use OR gates to get the carryOut value
        orGate.a.set(halfAdder1.carry.get());
        orGate.b.set(halfAdder2.carry.get());
        orGate.execute();

        carryOut.set(orGate.out.get());
    }
}

