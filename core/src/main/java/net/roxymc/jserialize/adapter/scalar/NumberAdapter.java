package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberAdapter extends AbstractNumberAdapter<Number> {
    public static final TypeAdapter<Number> INSTANCE = new NumberAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private NumberAdapter() {
        super(Number.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    static Number parseNumber(String value) throws NumberFormatException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
        }

        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            return new BigDecimal(value);
        } else {
            return new BigInteger(value);
        }
    }

    @Override
    protected Number fromLong(long value) {
        return value;
    }

    @Override
    protected Number fromDouble(double value) {
        return value;
    }

    @Override
    protected Number parse(String value) throws NumberFormatException {
        return parseNumber(value);
    }

    @Override
    protected void write(Writer writer, Number value) throws IOException {
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            writer.writeInt(value.intValue());
        } else if (value instanceof Long) {
            writer.writeLong(value.longValue());
        } else if (value instanceof Double || value instanceof Float) {
            writer.writeDouble(value.doubleValue());
        } else {
            writer.writeString(value.toString());
        }
    }
}
