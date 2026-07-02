package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.TypeAdapter;

import java.io.IOException;

public final class FloatAdapter extends AbstractNumberAdapter<Float> {
    public static final TypeAdapter<Float> PRIMITIVE = new FloatAdapter(float.class);
    public static final TypeAdapter<Float> BOXED = new FloatAdapter(Float.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private FloatAdapter(Class<Float> type) {
        super(type);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Float fromLong(long value) {
        float floatValue = (float) value;

        if ((long) floatValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return floatValue;
    }

    @Override
    protected Float fromDouble(double value) {
        float floatValue = (float) value;

        if (Double.isFinite(value) && (Float.isInfinite(floatValue) || (double) floatValue != value)) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return floatValue;
    }

    @Override
    protected Float parse(String value) throws NumberFormatException {
        return Float.parseFloat(value);
    }

    @Override
    protected void write(Writer writer, Float value) throws IOException {
        writer.writeDouble(value);
    }
}
