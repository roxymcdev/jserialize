package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class DoubleAdapter extends AbstractNumberAdapter<Double> {
    DoubleAdapter() {
        super(Double.class);
    }

    @Override
    protected Double fromLong(long value) {
        double doubleValue = (double) value;

        if ((long) doubleValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow or loss of precision: " + value);
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
