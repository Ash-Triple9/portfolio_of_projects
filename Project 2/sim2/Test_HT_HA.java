/* Testcase for 252 Sim 2.
 *
 * Author: Hamlet Taraz (Mostly ripped from Russ Lewis)
 */

public class Test_HT_HA {
    public static void main(String[] args) {
        Sim2_HalfAdder p0 = new Sim2_HalfAdder();
        Sim2_HalfAdder p1 = new Sim2_HalfAdder();
        Sim2_HalfAdder p2 = new Sim2_HalfAdder();
        Sim2_HalfAdder p3 = new Sim2_HalfAdder();

        p0.a.set(false);
        p0.b.set(false);
        p1.a.set(false);
        p1.b.set(true);
        p2.a.set(true);
        p2.b.set(false);
        p3.a.set(true);
        p3.b.set(true);

        p0.execute();
        p1.execute();
        p2.execute();
        p3.execute();

        System.out.printf("%c + %c = %c%c\r\n", bit(p0.a.get()), bit(p0.b.get()), bit(p0.carry.get()), bit(p0.sum.get()));
        System.out.printf("%c + %c = %c%c\r\n", bit(p1.a.get()), bit(p1.b.get()), bit(p1.carry.get()), bit(p1.sum.get()));
        System.out.printf("%c + %c = %c%c\r\n", bit(p2.a.get()), bit(p2.b.get()), bit(p2.carry.get()), bit(p2.sum.get()));
        System.out.printf("%c + %c = %c%c\r\n", bit(p3.a.get()), bit(p3.b.get()), bit(p3.carry.get()), bit(p3.sum.get()));
    }

    public static char bit(boolean b) {
        if (b)
            return '1';
        else
            return '0';
    }
}
