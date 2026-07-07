package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.token.TokenType.*;

public final class TokenTypes {
    public static final TokenType.Valued<String> NAME = valued("name", Kind.NAME, NameToken::new);
    public static final TokenType.NonValued OBJECT_START = nonValued("object_start", Kind.STRUCTURE_START, () -> Tokens.OBJECT_START);
    public static final TokenType.NonValued OBJECT_END = nonValued("object_end", Kind.STRUCTURE_END, () -> Tokens.OBJECT_END);
    public static final TokenType.NonValued ARRAY_START = nonValued("array_start", Kind.STRUCTURE_START, () -> Tokens.ARRAY_START);
    public static final TokenType.NonValued ARRAY_END = nonValued("array_end", Kind.STRUCTURE_END, () -> Tokens.ARRAY_END);
    public static final TokenType.Valued<String> STRING = valued("string", Kind.SCALAR, StringToken::new);
    public static final TokenType.Valued<Boolean> BOOLEAN = valued("boolean", Kind.SCALAR, BooleanToken::of);
    public static final TokenType.Valued<Integer> INT = valued("int", Kind.SCALAR, IntToken::new);
    public static final TokenType.Valued<Long> LONG = valued("long", Kind.SCALAR, LongToken::new);
    public static final TokenType.Valued<Double> DOUBLE = valued("double", Kind.SCALAR, DoubleToken::new);
    public static final TokenType.Virtual NUMERIC = virtual("numeric", Kind.SCALAR);
    public static final TokenType.Valued<byte[]> BINARY = valued("binary", Kind.SCALAR, BinaryToken::new);
    public static final TokenType.NonValued NULL = nonValued("null", Kind.NULL, () -> Tokens.NULL);
    public static final TokenType.Virtual END = virtual("end", Kind.END);

    private TokenTypes() {
    }
}
