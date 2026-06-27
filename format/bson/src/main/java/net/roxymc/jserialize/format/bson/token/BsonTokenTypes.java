package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenType.Kind;
import org.bson.BsonBinary;
import org.bson.BsonDbPointer;
import org.bson.BsonRegularExpression;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import static net.roxymc.jserialize.token.TokenType.nonValued;
import static net.roxymc.jserialize.token.TokenType.valued;

public final class BsonTokenTypes {
    public static final TokenType.Valued<BsonBinary> BINARY = valued(Kind.SCALAR, BsonBinaryToken::new);
    public static final TokenType.NonValued UNDEFINED = nonValued(Kind.NULL, () -> BsonTokens.UNDEFINED);
    public static final TokenType.Valued<ObjectId> OBJECT_ID = valued(Kind.SCALAR, ObjectIdToken::new);
    public static final TokenType.Valued<Long> DATE_TIME = valued(Kind.SCALAR, DateTimeToken::new);
    public static final TokenType.Valued<BsonRegularExpression> REGULAR_EXPRESSION = valued(Kind.SCALAR, RegularExpressionToken::new);
    public static final TokenType.Valued<BsonDbPointer> DB_POINTER = valued(Kind.SCALAR, DbPointerToken::new);
    public static final TokenType.Valued<String> JAVA_SCRIPT = valued(Kind.SCALAR, JavaScriptToken::new);
    public static final TokenType.Valued<String> SYMBOL = valued(Kind.SCALAR, SymbolToken::new);
    public static final TokenType.Valued<String> JAVA_SCRIPT_WITH_SCOPE = valued(Kind.SCALAR, JavaScriptWithScopeToken::new);
    public static final TokenType.Valued<Decimal128> DECIMAL_128 = valued(Kind.SCALAR, Decimal128Token::new);
    public static final TokenType.NonValued MIN_KEY = nonValued(Kind.NULL, () -> BsonTokens.MIN_KEY);
    public static final TokenType.NonValued MAX_KEY = nonValued(Kind.NULL, () -> BsonTokens.MAX_KEY);

    private BsonTokenTypes() {
    }
}
