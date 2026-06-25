package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class ByteAdapter extends AbstractNumberAdapter<Byte> {
    ByteAdapter() {
        super(Byte.class);
    }

    @Override
    protected Byte fromLong(long value) {
        byte byteValue = (byte) value;

        if (byteValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow: " + value);
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
