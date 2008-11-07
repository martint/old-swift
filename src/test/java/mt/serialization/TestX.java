package mt.serialization;

import com.facebook.thrift.TException;

import java.util.Iterator;
import java.util.Map;

public class TestX
{
	void x(Iterator iterator)
		throws TException
	{
		Map.Entry entry = (Map.Entry) iterator.next();
		y(((Double)entry.getKey()).doubleValue(), ((Integer) entry.getValue()).intValue());
	}

	void y(double d, int l) {}
}
