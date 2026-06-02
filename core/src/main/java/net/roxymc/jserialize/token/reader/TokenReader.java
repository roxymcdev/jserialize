package net.roxymc.jserialize.token.reader;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.*;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TokenReader<V> extends AbstractReader {
    private final Tokenizer<V> tokenizer;

    public TokenReader(Tokenizer<V> tokenizer) {
        this.tokenizer = nonNull(tokenizer, "tokenizer");
    }

    public Tokenizer<V> tokenizer() {
        return tokenizer;
    }

    @Override
    public TokenType peek() {
        if (!tokenizer.hasNext()) {
            return TokenType.END;
        }

        return tokenizer.peek();
    }

    private <T extends Token> T pop(Class<T> type) {
        return type.cast(tokenizer.next());
    }

    private void pop(Token expected) {
        if (tokenizer.next() != expected) {
            throw new IllegalStateException("token mismatch");
        }
    }

    @Override
    public String readName() {
        checkToken(peek(), TokenType.NAME);
        return pop(NameToken.class).value();
    }

    @Override
    public void readObjectStart() {
        checkToken(peek(), TokenType.OBJECT_START);
        pop(Token.OBJECT_START);
    }

    @Override
    public void readObjectEnd() {
        checkToken(peek(), TokenType.OBJECT_END);
        pop(Token.OBJECT_END);
    }

    @Override
    public void readArrayStart() {
        checkToken(peek(), TokenType.ARRAY_START);
        pop(Token.ARRAY_START);
    }

    @Override
    public void readArrayEnd() {
        checkToken(peek(), TokenType.ARRAY_END);
        pop(Token.ARRAY_END);
    }

    @Override
    public String readString() {
        checkToken(peek(), TokenType.STRING);
        return pop(StringToken.class).value();
    }

    @Override
    public boolean readBoolean() {
        checkToken(peek(), TokenType.BOOLEAN);
        return pop(BooleanToken.class).booleanValue();
    }

    @Override
    public int readInt() {
        checkToken(peek(), TokenType.INT);
        return pop(IntToken.class).intValue();
    }

    @Override
    public long readLong() {
        checkToken(peek(), TokenType.LONG);
        return pop(LongToken.class).longValue();
    }

    @Override
    public double readDouble() {
        checkToken(peek(), TokenType.DOUBLE);
        return pop(DoubleToken.class).doubleValue();
    }

    @Override
    public byte[] readBinary() {
        checkToken(peek(), TokenType.BINARY);
        return pop(BinaryToken.class).value();
    }

    @Override
    public void readNull() {
        checkToken(peek(), TokenType.NULL);
        pop(ScalarToken.NULL);
    }

    @Override
    public void skipValue() {
        tokenizer.skipValue();
    }
}
