package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;

import java.io.IOException;

public final class DoubleAdapter extends AbstractNumberAdapter<Double> {
    public static final TypeAdapter<Double> PRIMITIVE = new DoubleAdapter(double.class);
    public static final TypeAdapter<Double> BOXED = new DoubleAdapter(Double.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private DoubleAdapter(Class<Double> type) {
        super(type);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Double fromLong(long value) {
        double doubleValue = (double) value;

        if ((long) doubleValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return doubleValue;
    }

    @Override
    protected Double fromDouble(double value) {
        return value;
    }

    @Override
    protected Double parse(String value) throws NumberFormatException {
        return Double.parseDouble(value);
    }

    @Override
    protected void write(Writer writer, Double value) throws IOException {
        writer.writeDouble(value);
    }
}
