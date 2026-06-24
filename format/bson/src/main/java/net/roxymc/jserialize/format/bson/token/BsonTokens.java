package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.Token;

public final class BsonTokens {
    public static final Token UNDEFINED = () -> BsonTokenTypes.UNDEFINED;
    public static final Token MIN_KEY = () -> BsonTokenTypes.MIN_KEY;
    public static final Token MAX_KEY = () -> BsonTokenTypes.MAX_KEY;

    private BsonTokens() {
    }
}
