package littlebot2014.littlebotdriverstation;

public class IntMan {

	public static byte int4(int i)	//Returns the low byte of a short.
	{
		return (byte)(i & 0xFF);
	}
	public static byte int3(int i)	//Returns the high byte of a short.
	{
		return (byte)((i & 0xFF00)>>8);
	}
	public static byte int2(int i)
	{
		return (byte)((i & 0xFF0000)>>16);
	}
	public static int int1(int i)
	{
		return (byte)((i & 0xFF000000)>>24);
	}

}
