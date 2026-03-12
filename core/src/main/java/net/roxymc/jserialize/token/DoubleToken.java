package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

public final class DoubleToken implements NumberToken<Double> {
    private final double value;

    public DoubleToken(double value) {
        this.value = value;
    }

    public double doubleValue() {
        return value;
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.writeDouble(doubleValue());
    }

    @Override
    public TokenType type() {
        return TokenType.DOUBLE;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DoubleToken)) {
            return false;
        }

        DoubleToken that = (DoubleToken) obj;
        return this.value == that.value;
    }
}
