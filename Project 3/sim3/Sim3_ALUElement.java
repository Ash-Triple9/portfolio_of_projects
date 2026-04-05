//Author: AShiqul Alam
public class Sim3_ALUElement {
    public RussWire[] aluOp;
    public RussWire bInvert, a, b, carryIn, less;
    public RussWire result, addResult, carryOut;

    public boolean AND;
    public boolean OR;
    public boolean XOR;
    public boolean bVal;
    public boolean lessAND;

    public Sim2_FullAdder adder;
    public Sim3_MUX_8by1 mux;


    public Sim3_ALUElement() {
        aluOp = new RussWire[3];
        for (int i = 0; i < aluOp.length; i++) {
            aluOp[i] = new RussWire();
        }

        bInvert = new RussWire();
        a = new RussWire();
        b = new RussWire();
        carryIn = new RussWire();
        less = new RussWire();

        result = new RussWire();
        addResult = new RussWire();
        carryOut = new RussWire();

        adder = new Sim2_FullAdder();

        mux = new Sim3_MUX_8by1();

    }

    public void execute_pass1() {

        // Check for bNegate first, and set b's value accordingly
        bVal = bInvert.get() ? !b.get() : b.get();

        // Add operation
        adder.a.set(a.get());
        adder.b.set(bVal);
        this.adder.carryIn.set(carryIn.get());
        adder.execute();

        addResult.set(adder.sum.get());
        carryOut.set(adder.carryOut.get());




    }

    public void execute_pass2() {
        
        for (int i = 0; i < 3; i++) {
            mux.control[i].set(aluOp[i].get());
        }
        // Set & execute the MUX

        mux.in[0].set(a.get() && bVal); // AND
        mux.in[1].set(a.get() || bVal); // OR
        mux.in[2].set(addResult.get()); // ADD
        mux.in[3].set(less.get()); // LESS
        mux.in[4].set(a.get() ^ bVal); // XOR
        // set the rest of the values to false, since they are never used.
        mux.in[5].set(false);
        mux.in[6].set(false);
        mux.in[7].set(false);

        mux.execute();
        result.set(mux.out.get());

    }

}
