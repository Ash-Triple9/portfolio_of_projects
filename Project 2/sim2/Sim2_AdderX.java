//Author: Ashiqul Alam
public class Sim2_AdderX {
    public RussWire[] a, b, sum;
    public RussWire carryOut, overflow;

    private Sim2_FullAdder[] fullAdders;

    public Sim2_AdderX(int X) {
        a = new RussWire[X];
        b = new RussWire[X];
        sum = new RussWire[X];
        fullAdders = new Sim2_FullAdder[X];

        for (int i = 0; i < X; i++) {
            a[i] = new RussWire();
            b[i] = new RussWire();
            sum[i] = new RussWire();
            fullAdders[i] = new Sim2_FullAdder();
        }

        carryOut = new RussWire();
        overflow = new RussWire();
    }

    public void execute() {
        RussWire carry = new RussWire();
        carry.set(false);

        for (int i = 0; i < fullAdders.length; i++) {

            fullAdders[i].a.set(a[i].get());
            fullAdders[i].b.set(b[i].get());
            fullAdders[i].carryIn.set(carry.get());

            fullAdders[i].execute();

            carry = fullAdders[i].carryOut;

            sum[i].set(fullAdders[i].sum.get());
        }

        carryOut.set(carry.get());
        overflow.set(carryOut.get() ^ fullAdders[fullAdders.length - 1].carryIn.get());
    }
}
