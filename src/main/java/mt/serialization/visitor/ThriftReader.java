package mt.serialization.visitor;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TType;
import mt.serialization.schema.BinaryType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

public class ThriftReader
	implements Visitor
{
	private final TProtocol protocol;

	public ThriftReader(TProtocol protocol)
	{
		this.protocol = protocol;
	}

	public void visit(StructureType type)
	{
		try {
			protocol.readStructBegin();
			while (true) {
				TField tfield = protocol.readFieldBegin();
				if (tfield.type == TType.STOP) {
					break;
				}

				Field field = type.getField(tfield.id);

				if (field == null || field.getType().getTType() != tfield.type) {
					TProtocolUtil.skip(protocol, tfield.type);
				}
				else {
					field.accept(this);
				}
				protocol.readFieldEnd();
			}
			protocol.readStructEnd();
		}
		catch (TException e) {
			throw new RuntimeException(e);
		}
	}

	public void visit(Field type)
	{

	}

	public void visit(ListType type)
	{
		try {
			TList tlist = protocol.readListBegin();

			// todo where to store values? should we pass a ListVisitor that collects them?
			Type valueType = type.getValueType();
			for (int i = 0; i < tlist.size; ++i) {
				valueType.accept(this);
			}
			
			protocol.readListEnd();
		}
		catch (TException e) {
			throw new RuntimeException(e);
		}
	}

	public void visit(BinaryType type)
	{
		try {
			byte[] value = protocol.readBinary();

		}
		catch (TException e) {
			throw new RuntimeException(e);
		}
	}

}
