//Author: Ashiqul Alam
public class Sim3_MUX_8by1 {
    public RussWire[] control, in;
    public RussWire out;


    public Sim3_MUX_8by1() {
    control = new RussWire[3];
    for (int i = 0; i<3; i++) {
        control[i] = new RussWire();
    }

    in = new RussWire[8];
    for (int j = 0; j<8; j++) {
        in[j] = new RussWire();
    }

    out = new RussWire();


}

public void execute() {

    // Convert the control bits to an index using bitwise operations
    int index = (control[2].get() ? 4 : 0) | (control[1].get() ? 2 : 0) | (control[0].get() ? 1 : 0);
        
    // Set the output based on the selected input
    out.set(in[index].get());
}
}
