package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class IntegerAdapter extends AbstractNumberAdapter<Integer> {
    IntegerAdapter() {
        super(Integer.class);
    }

    @Override
    protected Integer fromLong(long value) {
        int intValue = (int) value;

        if (intValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow: " + value);
        }

        return intValue;
    }

    @Override
    protected Integer parse(String value) throws NumberFormatException {
        return Integer.parseInt(value);
    }

    @Override
    protected void write(Writer writer, Integer value) throws IOException {
        writer.writeInt(value);
    }
}
