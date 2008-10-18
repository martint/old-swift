package mt.serialization.event;

import com.facebook.thrift.protocol.TType;

public interface StructureVisitor
{
	void begin();
	void end();

	void beginField(short id, TType type);
	void endField();

	void beginMap(int size);
	void endMap();

	void beginSet(int size);
	void endSet();

	void beginList(int size);
	void endList();

	void beginStructure();
	void endStructure();
}
