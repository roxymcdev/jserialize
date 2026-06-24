package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.token.TokenType.*;

public final class TokenTypes {
    public static final TokenType.Valued<String> NAME = valued(Kind.NAME, NameToken::new);
    public static final TokenType.NonValued OBJECT_START = nonValued(Kind.STRUCTURE_START, () -> Tokens.OBJECT_START);
    public static final TokenType.NonValued OBJECT_END = nonValued(Kind.STRUCTURE_END, () -> Tokens.OBJECT_END);
    public static final TokenType.NonValued ARRAY_START = nonValued(Kind.STRUCTURE_START, () -> Tokens.ARRAY_START);
    public static final TokenType.NonValued ARRAY_END = nonValued(Kind.STRUCTURE_END, () -> Tokens.ARRAY_END);
    public static final TokenType.Valued<String> STRING = valued(Kind.SCALAR, StringToken::new);
    public static final TokenType.Valued<Boolean> BOOLEAN = valued(Kind.SCALAR, BooleanToken::of);
    public static final TokenType.Valued<Integer> INT = valued(Kind.SCALAR, IntToken::new);
    public static final TokenType.Valued<Long> LONG = valued(Kind.SCALAR, LongToken::new);
    public static final TokenType.Valued<Double> DOUBLE = valued(Kind.SCALAR, DoubleToken::new);
    public static final TokenType.Virtual NUMERIC = virtual(Kind.SCALAR);
    public static final TokenType.Valued<byte[]> BINARY = valued(Kind.SCALAR, BinaryToken::new);
    public static final TokenType.NonValued NULL = nonValued(Kind.NULL, () -> Tokens.NULL);
    public static final TokenType.Virtual END = virtual(Kind.END);

    private TokenTypes() {
    }
}
