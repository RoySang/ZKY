package sict.zky.packet;

public class CircleBuf
{
	int NMAX = 16;

	int iput = 0;

	int iget = 0;

	int n = 0;

	char buffer[] = new char[16];

	public int addring(int i)
	{
		return (i + 1) == NMAX ? 0 : i + 1;
	}

	public char get()
	{
		int pos;
		if (n > 0)
		{
			pos = iget;
			iget = addring(iget);
			n--;

			return buffer[pos];

		} else
		{
			return 0;
		}
	}

	public void put(char z)
	{
		if (n < NMAX)
		{
			buffer[iput] = z;

			iput = addring(iput);
			n++;
		} else
		{
			buffer[0] = z;
		}
		;
	}

	public boolean isEmpty()
	{
		if (n == 0)
			return true;
		return false;
	}

	public boolean isFull()
	{
		if (n == NMAX)
			return true;
		return false;
	}

}
