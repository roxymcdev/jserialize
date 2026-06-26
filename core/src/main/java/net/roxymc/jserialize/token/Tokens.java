package net.roxymc.jserialize.token;

public final class Tokens {
    public static final Token OBJECT_START = () -> TokenTypes.OBJECT_START;
    public static final Token OBJECT_END = () -> TokenTypes.OBJECT_END;
    public static final Token ARRAY_START = () -> TokenTypes.ARRAY_START;
    public static final Token ARRAY_END = () -> TokenTypes.ARRAY_END;
    public static final Token NULL = () -> TokenTypes.NULL;

    private Tokens() {
    }
}
