/* Implementation of a 32-bit adder in C.
 *
 * Author: Ashiqul Alam
 */

#include "sim1.h"

void execute_add(Sim1Data *obj)
{
	// TODO: implement me!

	int sub = obj->isSubtraction;
	int sum = 0;

	int carry = 0;
	for (int i = 0; i < 32; i++)
	{
		// extract the inputs
		int aBit = (obj->a >> i) & 1;
		int bBit = (obj->b >> i) & 1;
		int sumBit = 0;
		// check if operation asks to subtract
		if (sub == 1)
		{
			if (i == 0)
			{
				carry = 1;
			}
			if (bBit == 1)
			{
				bBit = 0;
			}
			else
			{
				bBit = 1;
			}
		}

		// make the choice
		if (aBit == 0 && bBit == 0 && carry == 0)
		{
			sumBit = 0;
			carry = 0;
		}
		else if (aBit == 0 && bBit == 0 && carry == 1)
		{
			sumBit = 1;
			carry = 0;
		}
		else if (aBit == 0 && bBit == 1 && carry == 0)
		{
			sumBit = 1;
			carry = 0;
		}
		else if (aBit == 0 && bBit == 1 && carry == 1)
		{
			sumBit = 0;
			carry = 1;
		}
		else if (aBit == 1 && bBit == 0 && carry == 0)
		{
			sumBit = 1;
			carry = 0;
		}
		else if (aBit == 1 && bBit == 0 && carry == 1)
		{
			sumBit = 0;
			carry = 1;
		}
		else if (aBit == 1 && bBit == 1 && carry == 0)
		{
			sumBit = 0;
			carry = 1;
		}
		else if (aBit == 1 && bBit == 1 && carry == 1)
		{
			sumBit = 1;
			carry = 1;
		}

		// save the answer
		obj->carryOut = carry;
		sum |= (sumBit << i);
	}

	// set flags
	obj->sum = sum;
	obj->sumNonNeg = !(obj->sum >> 31);
	obj->aNonNeg = !(obj->a >> 31);
	obj->bNonNeg = !(obj->b >> 31);

	if (!obj->isSubtraction)
	{
		obj->overflow = ((obj->aNonNeg == obj->bNonNeg) && (obj->bNonNeg != obj->sumNonNeg));
	}
	else
	{
		obj->overflow = ((obj->aNonNeg != obj->bNonNeg) && (obj->bNonNeg != obj->sumNonNeg));
	}
}
