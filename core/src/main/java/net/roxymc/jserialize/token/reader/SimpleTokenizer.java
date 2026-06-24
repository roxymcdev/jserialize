package net.roxymc.jserialize.token.reader;

import net.roxymc.jserialize.token.Token;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class SimpleTokenizer implements Tokenizer<Token> {
    private final Iterator<Token> iterator;
    private @Nullable Token token;

    public SimpleTokenizer(Iterator<Token> iterator) {
        this.iterator = nonNull(iterator, "iterator");
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public TokenType peek() {
        return peek0().type();
    }

    private Token peek0() {
        if (token == null) {
            token = iterator.next();
        }

        return token;
    }

    @Override
    public Token next() {
        Token token = peek0();
        this.token = null;
        return token;
    }

    @Override
    public Token nextValue() {
        return next();
    }

    @Override
    public void skipValue() {
        TokenType type = peek();

        if (!type.kind().marksValue()) {
            throw new IllegalStateException(type + "does not support this operation");
        }

        next(); // skip the value

        if (type == TokenTypes.OBJECT_START) {
            while (peek() != TokenTypes.OBJECT_END) {
                next(); // skip name
                skipValue();
            }

            next(); // skip object end
        } else if (type == TokenTypes.ARRAY_START) {
            while (peek() != TokenTypes.ARRAY_END) {
                skipValue();
            }

            next(); // skip array end
        }
    }
}
