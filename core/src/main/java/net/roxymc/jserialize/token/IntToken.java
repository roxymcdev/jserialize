package net.roxymc.jserialize.token;

public final class IntToken implements NumberToken<Integer> {
    private final int value;

    public IntToken(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public TokenType.Valued<Integer> type() {
        return TokenTypes.INT;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof IntToken)) {
            return false;
        }

        IntToken that = (IntToken) obj;
        return this.value == that.value;
    }
}
