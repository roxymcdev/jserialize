package net.roxymc.jserialize.token.writer;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.token.*;

public final class TokenWriter<V> implements Writer {
    private final Detokenizer<V> detokenizer;

    public TokenWriter(Detokenizer<V> detokenizer) {
        this.detokenizer = detokenizer;
    }

    public Detokenizer<V> detokenizer() {
        return detokenizer;
    }

    @Override
    public void writeName(String name) {
        detokenizer.accept(new NameToken(name));
    }

    @Override
    public void writeObjectStart() {
        detokenizer.accept(Token.OBJECT_START);
    }

    @Override
    public void writeObjectEnd() {
        detokenizer.accept(Token.OBJECT_END);
    }

    @Override
    public void writeArrayStart() {
        detokenizer.accept(Token.ARRAY_START);
    }

    @Override
    public void writeArrayEnd() {
        detokenizer.accept(Token.ARRAY_END);
    }

    @Override
    public void writeString(String value) {
        detokenizer.accept(new StringToken(value));
    }

    @Override
    public void writeBoolean(boolean value) {
        detokenizer.accept(value ? BooleanToken.TRUE : BooleanToken.FALSE);
    }

    @Override
    public void writeInt(int value) {
        detokenizer.accept(new IntToken(value));
    }

    @Override
    public void writeLong(long value) {
        detokenizer.accept(new LongToken(value));
    }

    @Override
    public void writeDouble(double value) {
        detokenizer.accept(new DoubleToken(value));
    }

    @Override
    public void writeBinary(byte[] value) {
        detokenizer.accept(new BinaryToken(value));
    }

    @Override
    public void writeNull() {
        detokenizer.accept(ScalarToken.NULL);
    }
}
