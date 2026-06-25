package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class ShortAdapter extends AbstractNumberAdapter<Short> {
    ShortAdapter() {
        super(Short.class);
    }

    @Override
    protected Short fromLong(long value) {
        short shortValue = (short) value;

        if (shortValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow: " + value);
        }

        return shortValue;
    }

    @Override
    protected Short parse(String value) throws NumberFormatException {
        return Short.parseShort(value);
    }

    @Override
    protected void write(Writer writer, Short value) throws IOException {
        writer.writeInt(value);
    }
}
