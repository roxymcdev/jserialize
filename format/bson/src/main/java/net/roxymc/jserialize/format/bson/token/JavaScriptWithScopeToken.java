package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractScalarToken;
import net.roxymc.jserialize.token.TokenType;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class JavaScriptWithScopeToken extends AbstractScalarToken<String> {
    private final String value;

    public JavaScriptWithScopeToken(String value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public TokenType.Valued<String> type() {
        return BsonTokenTypes.JAVA_SCRIPT_WITH_SCOPE;
    }
}
