package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

public final class LongToken implements NumberToken<Long> {
    private final long value;

    public LongToken(long value) {
        this.value = value;
    }

    public long longValue() {
        return value;
    }

    @Override
    public Long value() {
        return value;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.writeLong(longValue());
    }

    @Override
    public TokenType type() {
        return TokenType.LONG;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LongToken)) {
            return false;
        }

        LongToken that = (LongToken) obj;
        return this.value == that.value;
    }
}
