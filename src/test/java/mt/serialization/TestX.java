package mt.serialization;

import com.facebook.thrift.TException;

public class TestX
{
	void x(boolean value)
		throws TException
	{
		if (value) {
			System.out.println("x");
		}
		else {
			System.out.println("y");
		}
	}
}
