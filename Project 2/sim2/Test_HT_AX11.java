/* Testcase for 252 Sim 2.
 *
 * Author: Hamlet Taraz (Mostly ripped from Russ Lewis)
 */

public class Test_HT_AX11 {
    public static void main(String[] args) {
        Sim2_AdderX p = new Sim2_AdderX(64);

        for (int i = 0; i < 64; i++) {
            p.a[i].set(i % 13 % 7 >= 1);
            p.b[i].set(i % 9 % 4 <= 2);
        }

        p.execute();

        System.out.printf("  ");
        print_bits(p.a);
        System.out.printf("\r\n");

        System.out.printf("+ ");
        print_bits(p.b);
        System.out.printf("\r\n");

        for (int i = 0; i < p.a.length + 2; i++)
            System.out.printf("-");
        System.out.printf("\r\n");

        System.out.printf("  ");
        print_bits(p.sum);
        System.out.printf("\r\n");

        System.out.printf("\r\n");
        System.out.printf("  carryOut = %c\r\n", bit(p.carryOut.get()));
        System.out.printf("  overflow = %c\r\n", bit(p.overflow.get()));
    }

    public static void print_bits(RussWire[] bits) {
        for (int i = bits.length - 1; i >= 0; i--) {
            if (bits[i].get())
                System.out.print("1");
            else
                System.out.print("0");
        }
    }

    public static char bit(boolean b) {
        if (b)
            return '1';
        else
            return '0';
    }
}
