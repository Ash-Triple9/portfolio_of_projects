/* Testcase for 252 Sim 2.
 *
 * Author: Hamlet Taraz (Mostly ripped from Russ Lewis)
 */

public class Test_HT_FA {
    public static void main(String[] args) {
        Sim2_FullAdder[] p = new Sim2_FullAdder[8];
        for (int i = 0; i < 8; i++) {
            p[i] = new Sim2_FullAdder();
            p[i].b.set(i % 2 >= 1);
            p[i].a.set(i % 4 >= 2);
            p[i].carryIn.set(i % 8 >= 4);
            p[i].execute();
            System.out.printf("%c + %c + %c = %c%c\r\n", bit(p[i].carryIn.get()), bit(p[i].a.get()), bit(p[i].b.get()), bit(p[i].carryOut.get()), bit(p[i].sum.get()));
        }
    }

    public static char bit(boolean b) {
        if (b)
            return '1';
        else
            return '0';
    }
}
