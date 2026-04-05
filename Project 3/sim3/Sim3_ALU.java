// Author: Ashiqul Alam
public class Sim3_ALU {
    public RussWire[] aluOp, a, b;
    public RussWire bNegate;
    public RussWire[] result;
    public int length;

    public Sim3_ALUElement[] alu;

    public Sim3_ALU(int X) {
        a = new RussWire[X];
        b = new RussWire[X];
        aluOp = new RussWire[3];
        result = new RussWire[X];
        length = X;

        alu = new Sim3_ALUElement[X];
        bNegate = new RussWire();

        for (int i = 0; i < X; i++) {
            a[i] = new RussWire();
            b[i] = new RussWire();
            result[i] = new RussWire();
            alu[i] = new Sim3_ALUElement();
        }
        for (int j = 0; j < 3; j++) {
            aluOp[j] = new RussWire();
        }
    }

    public void execute() {
        // put these values in our multi-bit alu
        // set a and b values
        alu[0].a.set(a[0].get());
        alu[0].b.set(b[0].get());

        // set bInvert value
        alu[0].bInvert.set(bNegate.get());

        // set operation values
        alu[0].aluOp[0].set(aluOp[0].get());
        alu[0].aluOp[1].set(aluOp[1].get());
        alu[0].aluOp[2].set(aluOp[2].get());

        // First alu's carryIn value is the value of bNegate
        alu[0].carryIn.set(bNegate.get());

        // execute_pass1 for alu[0]
        alu[0].execute_pass1();


        for (int i = 1; i < length; i++) {

            // put these values in our multi-bit alu
            // set a and b values
            alu[i].a.set(a[i].get());
            alu[i].b.set(b[i].get());

            // set bInvert value
            alu[i].bInvert.set(bNegate.get());

            // set operation values
            alu[i].aluOp[0].set(aluOp[0].get());
            alu[i].aluOp[1].set(aluOp[1].get());
            alu[i].aluOp[2].set(aluOp[2].get());

            // set the carryIn values
            alu[i].carryIn.set(alu[i-1].carryOut.get());

            // execute_pass1 for each alu
            alu[i].execute_pass1();

        }

        alu[0].less.set(alu[length - 1].addResult.get());
        // Set all values of less except for the first alu to be false
        for (int i = 1; i < length; i++) {
            alu[i].less.set(false);
        }

        for (int i = 0; i < length; i++) {
            alu[i].execute_pass2();
            result[i].set(alu[i].result.get());
        }
    }
}
