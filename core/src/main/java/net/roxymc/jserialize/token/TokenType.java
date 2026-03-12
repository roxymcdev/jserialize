package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.util.IOFunction;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public enum TokenType {
    NAME(Kind.MARKER, reader -> new NameToken(reader.readName())),
    OBJECT_START(Kind.VALUE, reader -> {
        reader.readObjectStart();
        return Token.OBJECT_START;
    }),
    OBJECT_END(Kind.MARKER, reader -> {
        reader.readObjectEnd();
        return Token.OBJECT_END;
    }),
    ARRAY_START(Kind.VALUE, reader -> {
        reader.readArrayStart();
        return Token.ARRAY_START;
    }),
    ARRAY_END(Kind.MARKER, reader -> {
        reader.readArrayEnd();
        return Token.ARRAY_END;
    }),
    STRING(Kind.VALUE_SCALAR, reader -> new StringToken(reader.readString())),
    BOOLEAN(Kind.VALUE_SCALAR, reader -> reader.readBoolean() ? BooleanToken.TRUE : BooleanToken.FALSE),
    INT(Kind.VALUE_SCALAR, reader -> new IntToken(reader.readInt())),
    LONG(Kind.VALUE_SCALAR, reader -> new LongToken(reader.readLong())),
    DOUBLE(Kind.VALUE_SCALAR, reader -> new DoubleToken(reader.readDouble())),
    NUMBER(Kind.VALUE_SCALAR, reader -> {
        TokenType peek = reader.peek();

        switch (peek) {
            case INT:
            case LONG:
                return peek.read(reader);
            default:
                return DOUBLE.read(reader);
        }
    }),
    BINARY(Kind.VALUE_SCALAR, reader -> new BinaryToken(reader.readBinary())),
    NULL(Kind.VALUE_SCALAR, reader -> {
        reader.readNull();
        return ScalarToken.NULL;
    }),
    UNKNOWN(Kind.VALUE_SCALAR, null),
    END(Kind.MARKER, null),
    ;

    private final Kind kind;
    private final @Nullable IOFunction<Reader, Token> read;

    TokenType(Kind kind, @Nullable IOFunction<Reader, Token> read) {
        this.kind = kind;
        this.read = read;
    }

    public Kind kind() {
        return kind;
    }

    public Token read(Reader reader) throws IOException {
        if (read == null) {
            throw new UnsupportedOperationException(this + " does not support this operation");
        }

        return read.apply(reader);
    }

    public enum Kind {
        MARKER,
        VALUE,
        VALUE_SCALAR,
        ;

        public boolean isValue() {
            return this == VALUE || this == VALUE_SCALAR;
        }
    }
}
