/* Testcase for 252 Sim 1.
 *
 * Author: Nathan Oswald
*/

public class Test_N1_COMP
{
	public static void main(String[] args)
	{
		Sim1_2sComplement p = new Sim1_2sComplement();

        p.in[0].set(false);
        p.in[31].set(false);
        for (int i = 1; i < 31; i++) {
            p.in[i].set(true);
        }

        
        printfunc(p);

        p = new Sim1_2sComplement();

        p.in[0].set(false);
        p.in[1].set(true);
        for (int i = 2; i < 32; i++) {
            p.in[i].set(false);
        }

        printfunc(p);

        p = new Sim1_2sComplement();

        p.in[0].set(true);
        for (int i = 1; i < 32; i++) {
            p.in[i].set(false);
        }

        printfunc(p);

        p = new Sim1_2sComplement();

        for (int i = 0; i < 32; i++) {
            p.in[i].set(false);
        }

        printfunc(p);

        p = new Sim1_2sComplement();

        p.in[31].set(false);
        for (int i = 0; i < 31; i++) {
            p.in[i].set(true);
        }

        printfunc(p);

        p = new Sim1_2sComplement();
        Sim1_2sComplement p2 = new Sim1_2sComplement();
        for (int i = 0; i <32; i++) {
            p.in[i].set(i%2 == 1);
            p2.in[i].set(i%2 == 0);
        }

        printfunc(p);
        printfunc(p2);
	}

    public static void printfunc(Sim1_2sComplement p) {
        p.execute();

		System.out.printf("~  ");
		print_bits(p.in);
		System.out.printf("\n");

		System.out.printf("-----------------------------------\n");

		System.out.printf("   ");
		print_bits(p.out);
		System.out.printf("\n");

        System.out.printf("\n");
		
    }


	public static void print_bits(RussWire[] bits)
	{
		for (int i=31; i>=0; i--)
		{
			if (bits[i].get())
				System.out.print("1");
			else
				System.out.print("0");
		}
	}

	public static char bit(boolean b)
	{
		if (b)
			return '1';
		else
			return '0';
	}
}