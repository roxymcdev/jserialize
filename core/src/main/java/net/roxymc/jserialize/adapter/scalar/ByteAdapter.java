package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;

import java.io.IOException;

public final class ByteAdapter extends AbstractNumberAdapter<Byte> {
    public static final TypeAdapter<Byte> PRIMITIVE = new ByteAdapter(byte.class);
    public static final TypeAdapter<Byte> BOXED = new ByteAdapter(Byte.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private ByteAdapter(Class<Byte> type) {
        super(type);
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    protected Byte fromLong(long value) {
        byte byteValue = (byte) value;

        if (byteValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow: " + value);
        }

        return byteValue;
    }

    @Override
    protected Byte parse(String value) throws NumberFormatException {
        return Byte.parseByte(value);
    }

    @Override
    protected void write(Writer writer, Byte value) throws IOException {
        writer.writeInt(value);
    }
}
