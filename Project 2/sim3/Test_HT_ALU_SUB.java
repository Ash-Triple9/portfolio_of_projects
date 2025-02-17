/* Tester class for the ALU: tests the AND operation in a 32-bit ALU.
 * As with the MUX tester classes, we'll define a helper function in the
 * first class, which is then used in several variants, to reduce duplication.
 *
 * Author: Hamlet Taraz; mostly stolen from Russ Lewis (Test_30_ALU_32bit_AND)
 */

import java.util.Random;

public class Test_HT_ALU_SUB {
	public static void main(String[] args) {
		test(2, true, 0x1d00d00d);
	}

	public static void test(int op, boolean bNegate, int seed) {
		/*
		 * we build a Random object, with the seed provided, so that
		 * we can generate the various inputs.
		 */
		Random rand = new Random(seed);

		/*
		 * build several different ALUs, so that we can check a
		 * variety of different inputs.
		 */
		Sim3_ALU[] alus = new Sim3_ALU[16];
		for (int i = 0; i < alus.length; i++) {
			int size = (i / 4) * 16 + 16;
			alus[i] = new Sim3_ALU(size);
		}

		/* set the aluOp and bNegate fields for each ALU */
		boolean c0 = (op & 0x1) == 1;
		boolean c1 = ((op >> 1) & 0x1) == 1;
		boolean c2 = ((op >> 2) & 0x1) == 1;
		for (int i = 0; i < alus.length; i++) {
			alus[i].aluOp[0].set(c0);
			alus[i].aluOp[1].set(c1);
			alus[i].aluOp[2].set(c2);
			alus[i].bNegate.set(bNegate);
		}

		/* fill in the inputs with random data */
		for (int i = 0; i < alus.length; i++)
			for (int j = 0; j < alus[i].a.length; j++) {
				/*
				 * special case: to prevent any worry about
				 * overflow, we'll hard-code the MSB of both
				 * inputs to 0, if the op is LESS.
				 */
				if (op == 3 && j == alus[i].a.length - 1) {
					alus[i].a[j].set(false);
					alus[i].b[j].set(false);
					break;
				}

				alus[i].a[j].set(rand.nextBoolean());
				alus[i].b[j].set(rand.nextBoolean());
			}

		/* now, execute all of them! */
		for (int i = 0; i < alus.length; i++)
			alus[i].execute();

		/*
		 * finally, dump state. We'll try to avoid lots of long lines;
		 * we'll print out just enough information to make sure that the
		 * output is working.
		 */
		System.out.printf("op=%d bNegate=%b\n", op, bNegate);
		System.out.printf("-------------------\n");

		for (int i = 0; i < alus.length; i++) {
			System.out.print("\n");

			System.out.print(" a  = ");
			printBits(alus[i].a);

			System.out.print(" b  = ");
			printBits(alus[i].b);

			for (int j = 0; j <= alus[i].a.length / 4; j++)
				System.out.print("-----");
			System.out.print('\n');
			
			System.out.print("out = ");
			printBits(alus[i].result);

			System.out.print("\n");
		}
	}

	public static void printBits(RussWire[] bits) {
		for (int i = bits.length - 1; i >= 0; i--) {
			if (i < bits.length - 1 && i % 4 == 3)
				System.out.printf(" ");
			System.out.printf("%d", bits[i].get() ? 1 : 0);
		}

		System.out.printf("\n");
	}
}
