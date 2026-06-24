package net.roxymc.jserialize.token;

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
    public TokenType.Valued<Long> type() {
        return TokenTypes.LONG;
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
