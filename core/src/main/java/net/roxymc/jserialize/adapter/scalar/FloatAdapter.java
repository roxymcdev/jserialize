package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class FloatAdapter extends AbstractNumberAdapter<Float> {
    FloatAdapter() {
        super(Float.class);
    }

    @Override
    protected Float fromLong(long value) {
        float floatValue = (float) value;

        if ((long) floatValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return floatValue;
    }

    @Override
    protected Float fromDouble(double value) {
        float floatValue = (float) value;

        if (Double.isFinite(value) && (Float.isInfinite(floatValue) || (double) floatValue != value)) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow or loss of precision: " + value);
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
