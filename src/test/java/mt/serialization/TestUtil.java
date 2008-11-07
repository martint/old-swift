package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import mt.serialization.model.Field;
import mt.serialization.model.StructureType;
import static org.easymock.EasyMock.cmp;
import org.easymock.LogicalOperator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;

public class TestUtil
{
	public static TTestStruct deserialize(byte[] data)
		throws TException
	{
		TTestStruct result = new TTestStruct();
		result.read(new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(data))));

		return result;
	}

	public static TProtocol serialize(TTestStruct data)
		throws TException
	{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
		data.write(outputProtocol);

		return new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
	}


	public static TStruct toTStruct(StructureType type)
	{
		return new TStruct(type.getName());
	}

	public static TField toTField(Field field)
	{
		return new TField(field.getName(), field.getType().getTType(), field.getId());
	}

	public static TField equal(TField field)
	{
		return cmp(field, new Comparator<TField>()
		{
			public int compare(TField o1, TField o2)
			{
				int result = Short.valueOf(o1.id).compareTo(o2.id);

				if (result == 0) {
					result = Byte.valueOf(o1.type).compareTo(o2.type);
				}

				if (result == 0) {
					result = o1.name.compareTo(o2.name);
				}

				return result;
			}
		}, LogicalOperator.EQUAL);
	}


	public static interface WriteAction
	{
		public void write(TProtocol protocol)
			throws TException;
	}


	public static TStruct equal(TStruct struct)
	{
		return cmp(struct, new Comparator<TStruct>()
		{
			public int compare(TStruct o1, TStruct o2)
			{
				return o1.name.compareTo(o2.name);
			}
		}, LogicalOperator.EQUAL);
	}


}
