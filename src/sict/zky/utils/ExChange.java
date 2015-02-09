package sict.zky.utils;

public class ExChange
{

	public ExChange()
	{
		super();
	}

	public String exChange(String str)
	{
		StringBuffer sb = new StringBuffer();
		if (str != null)
		{
			for (int i = 0; i < str.length(); i++)
			{
				char c = str.charAt(i);
				if (Character.isLowerCase(c))
				{
					sb.append(Character.toUpperCase(c));
				}
			}
		}
		return sb.toString();
	}

}
