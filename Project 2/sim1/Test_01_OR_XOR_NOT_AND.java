/* Testcase for 252 Sim 1.
*
* Author: Frances Pearl McNabb
*/
public class Test_01_OR_XOR_NOT_AND
{
	public static void main(String[] args)
	{
		// testing or
		Sim1_OR x = new Sim1_OR();
		x.a.set(false);
		x.b.set(false);
		x.execute();
		System.out.println(x.out.get()); // false
		
		Sim1_OR y = new Sim1_OR();
		y.a.set(true);
		y.b.set(false);
		y.execute();
		System.out.println(y.out.get()); // true
		
		Sim1_OR z = new Sim1_OR();
		z.a.set(true);
		z.b.set(true);
		z.execute();
		System.out.println(z.out.get()); // true
		
		System.out.println();
		
		// testing xor
		Sim1_XOR a = new Sim1_XOR();
		a.a.set(false);
		a.b.set(false);
		a.execute();
		System.out.println(a.out.get()); // false
		
		Sim1_XOR b = new Sim1_XOR();
		b.a.set(true);
		b.b.set(false);
		b.execute();
		System.out.println(b.out.get()); // true
		
		Sim1_XOR c = new Sim1_XOR();
		c.a.set(true);
		c.b.set(true);
		c.execute();
		System.out.println(c.out.get()); // false
		
		System.out.println();
		
		// testing not
		Sim1_NOT d = new Sim1_NOT();
		d.in.set(false);
		d.execute();
		System.out.println(d.out.get()); // true
		
		Sim1_NOT e = new Sim1_NOT();
		e.in.set(true);
		e.execute();
		System.out.println(e.out.get()); // false
		
		System.out.println();
		
		// testing and
		Sim1_AND f = new Sim1_AND();
		f.a.set(true);
		f.b.set(true);
		f.execute();
		System.out.println(f.out.get()); // true
		
		Sim1_AND g = new Sim1_AND();
		g.a.set(true);
		g.b.set(false);
		g.execute();
		System.out.println(g.out.get()); // false
		
		Sim1_AND h = new Sim1_AND();
		h.a.set(false);
		h.b.set(false);
		h.execute();
		System.out.println(h.out.get()); // false
		
	}
		
}

