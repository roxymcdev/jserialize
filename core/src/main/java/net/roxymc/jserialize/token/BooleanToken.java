package net.roxymc.jserialize.token;

public final class BooleanToken implements ScalarToken<Boolean> {
    public static final BooleanToken TRUE = new BooleanToken(true);
    public static final BooleanToken FALSE = new BooleanToken(false);

    private final boolean value;

    private BooleanToken(boolean value) {
        this.value = value;
    }

    public static BooleanToken of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean booleanValue() {
        return value;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public TokenType.Valued<Boolean> type() {
        return TokenTypes.BOOLEAN;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof BooleanToken)) {
            return false;
        }

        BooleanToken that = (BooleanToken) obj;
        return this.value == that.value;
    }
}
