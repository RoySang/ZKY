package sict.zky.utils;

public class AddHex
{
	// private StringBuilder builder = new StringBuilder(6);

	public AddHex()
	{
		super();
	}

	public String add(String s1, String s2, String s3, String s4, String s5,
			String s6, String s7, String s8)
	{
		int x1 = Integer.parseInt(s1, 16);
		int x2 = Integer.parseInt(s2, 16);
		int x3 = Integer.parseInt(s3, 16);
		int x4 = Integer.parseInt(s4, 16);
		int x5 = Integer.parseInt(s5, 16);
		int x6 = Integer.parseInt(s6, 16);
		int x7 = Integer.parseInt(s7, 16);
		int x8 = Integer.parseInt(s8, 16);
		String sum = Integer.toHexString(x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8);
		int i = sum.length();
		while (6 - i > 0)
		{
			sum = sum + "0";
			i++;
		}
		return sum;
	}

}
