
public class Test_TCOMP_SUB {
	public static void main(String[] args)
	{
		
		Sim1_2sComplement a = new Sim1_2sComplement();
		// 10000000000010010000010111001111 - original binary
		// 01111111111101101111101000110000 - bitwise negation
		// 0111 1111 1111 0110 1111 1010 0011 0001 - add one, final answer
		a.in[ 0].set(true);
		a.in[ 1].set(true);
		a.in[ 2].set(true);
		a.in[ 3].set(true);
		a.in[ 4].set(false);
		a.in[ 5].set(false);
		a.in[ 6].set(true);
		a.in[ 7].set(true);
		a.in[ 8].set(true);
		a.in[ 9].set(false);
		a.in[10].set(true);
		a.in[11].set(false);
		a.in[12].set(false);
		a.in[13].set(false);
		a.in[14].set(false);
		a.in[15].set(false);
		a.in[16].set(true);
		a.in[17].set(false);
		a.in[18].set(false);
		a.in[19].set(true);
		a.in[20].set(false);
		a.in[21].set(false);
		a.in[22].set(false);
		a.in[23].set(false);
		a.in[24].set(false);
		a.in[25].set(false);
		a.in[26].set(false);
		a.in[27].set(false);
		a.in[28].set(false);
		a.in[29].set(false);
		a.in[30].set(false);
		a.in[31].set(true);
		
		a.execute();
		
		String twoComp = "";
		for (int i = 31; i >=0; i--) {
			if (a.out[i].get()) {
				twoComp += 1;
			}
			else {
				twoComp += 0;
			}
		}
		
		System.out.println(twoComp + "\n");
		
		Sim1_SUB z = new Sim1_SUB();
		
		// 1010 0000 1000 1010 0010 1000 1010 1100
		z.a[ 0].set(false);
		z.a[ 1].set(false);
		z.a[ 2].set(true);
		z.a[ 3].set(true);
		z.a[ 4].set(false);
		z.a[ 5].set(true);
		z.a[ 6].set(false);
		z.a[ 7].set(true);
		z.a[ 8].set(false);
		z.a[ 9].set(false);
		z.a[10].set(false);
		z.a[11].set(true);
		z.a[12].set(false);
		z.a[13].set(true);
		z.a[14].set(false);
		z.a[15].set(false);
		z.a[16].set(false);
		z.a[17].set(true);
		z.a[18].set(false);
		z.a[19].set(true);
		z.a[20].set(false);
		z.a[21].set(false);
		z.a[22].set(false);
		z.a[23].set(true);
		z.a[24].set(false);
		z.a[25].set(false);
		z.a[26].set(false);
		z.a[27].set(false);
		z.a[28].set(false);
		z.a[29].set(true);
		z.a[30].set(false);
		z.a[31].set(true);
		
		// 10000000000010010000010111001111 (should get two's complement from above)
		z.b[ 0].set(true);
		z.b[ 1].set(true);
		z.b[ 2].set(true);
		z.b[ 3].set(true);
		z.b[ 4].set(false);
		z.b[ 5].set(false);
		z.b[ 6].set(true);
		z.b[ 7].set(true);
		z.b[ 8].set(true);
		z.b[ 9].set(false);
		z.b[10].set(true);
		z.b[11].set(false);
		z.b[12].set(false);
		z.b[13].set(false);
		z.b[14].set(false);
		z.b[15].set(false);
		z.b[16].set(true);
		z.b[17].set(false);
		z.b[18].set(false);
		z.b[19].set(true);
		z.b[20].set(false);
		z.b[21].set(false);
		z.b[22].set(false);
		z.b[23].set(false);
		z.b[24].set(false);
		z.b[25].set(false);
		z.b[26].set(false);
		z.b[27].set(false);
		z.b[28].set(false);
		z.b[29].set(false);
		z.b[30].set(false);
		z.b[31].set(true);
		
		z.execute();
		// answer: 0010 0000 1000 0001 0010 0010 1101 1101

		System.out.printf("  ");
		print_bits(z.a);
		System.out.print("\n");

		System.out.printf("- ");
		print_bits(z.b);
		System.out.printf("\n");

		System.out.printf("----------------------------------\n");

		System.out.printf("  ");
		print_bits(z.sum);
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
