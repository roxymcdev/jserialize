package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

public final class ShortAdapter extends AbstractNumberAdapter<Short> {
    public static final ShortAdapter PRIMITIVE = new ShortAdapter(short.class);
    public static final ShortAdapter BOXED = new ShortAdapter(Short.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private ShortAdapter(Class<Short> type) {
        super(type);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Short fromLong(long value) {
        short shortValue = (short) value;

        if (shortValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow: " + value);
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
