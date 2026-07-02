package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;

import java.io.IOException;

public final class IntegerAdapter extends AbstractNumberAdapter<Integer> {
    public static final TypeAdapter<Integer> PRIMITIVE = new IntegerAdapter(int.class);
    public static final TypeAdapter<Integer> BOXED = new IntegerAdapter(Integer.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private IntegerAdapter(Class<Integer> type) {
        super(type);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Integer fromLong(long value) {
        int intValue = (int) value;

        if (intValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow: " + value);
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
