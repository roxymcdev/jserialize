package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.adapter.object.MapLike;
import net.roxymc.jserialize.format.TokenTypeRegistry;
import net.roxymc.jserialize.format.bson.token.BsonTokenTypes;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.*;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.Map;

final class BsonUtils implements FormatUtils<BsonValue> {
    static final TokenTypeRegistry<BsonType, BsonReader, BsonWriter> TOKEN_TYPES = TokenTypeRegistry.create(builder -> builder
            .bind(TokenTypes.NAME, null, BsonReader::readName, BsonWriter::writeName)
            .bind(TokenTypes.OBJECT_START, BsonType.DOCUMENT, BsonReader::readStartDocument, BsonWriter::writeStartDocument)
            .bind(TokenTypes.OBJECT_END, null, BsonReader::readEndDocument, BsonWriter::writeEndDocument)
            .bind(TokenTypes.ARRAY_START, BsonType.ARRAY, BsonReader::readStartArray, BsonWriter::writeStartArray)
            .bind(TokenTypes.ARRAY_END, null, BsonReader::readEndArray, BsonWriter::writeEndArray)
            .bind(TokenTypes.STRING, BsonType.STRING, BsonReader::readString, BsonWriter::writeString)
            .bind(TokenTypes.BOOLEAN, BsonType.BOOLEAN, BsonReader::readBoolean, BsonWriter::writeBoolean)
            .bind(TokenTypes.INT, BsonType.INT32, BsonReader::readInt32, BsonWriter::writeInt32)
            .bind(TokenTypes.LONG, BsonType.INT64, BsonReader::readInt64, BsonWriter::writeInt64)
            .bind(TokenTypes.DOUBLE, BsonType.DOUBLE, BsonReader::readDouble, BsonWriter::writeDouble)
            .bind(TokenTypes.BINARY, BsonType.BINARY,
                    reader -> reader.readBinaryData().getData(),
                    (writer, value) -> writer.writeBinaryData(new BsonBinary(value))
            )
            .bind(TokenTypes.NULL, BsonType.NULL, BsonReader::readNull, BsonWriter::writeNull)
            .bind(BsonTokenTypes.UNDEFINED, BsonType.UNDEFINED, BsonReader::readUndefined, BsonWriter::writeUndefined)
            .bind(BsonTokenTypes.OBJECT_ID, BsonType.OBJECT_ID, BsonReader::readObjectId, BsonWriter::writeObjectId)
            .bind(BsonTokenTypes.DATE_TIME, BsonType.DATE_TIME, BsonReader::readDateTime, BsonWriter::writeDateTime)
            .bind(BsonTokenTypes.REGULAR_EXPRESSION, BsonType.REGULAR_EXPRESSION, BsonReader::readRegularExpression, BsonWriter::writeRegularExpression)
            .bind(BsonTokenTypes.DB_POINTER, BsonType.DB_POINTER, BsonReader::readDBPointer, BsonWriter::writeDBPointer)
            .bind(BsonTokenTypes.JAVA_SCRIPT, BsonType.JAVASCRIPT, BsonReader::readJavaScript, BsonWriter::writeJavaScript)
            .bind(BsonTokenTypes.SYMBOL, BsonType.SYMBOL, BsonReader::readSymbol, BsonWriter::writeSymbol)
            .bind(BsonTokenTypes.JAVA_SCRIPT_WITH_SCOPE, BsonType.JAVASCRIPT_WITH_SCOPE, BsonReader::readJavaScriptWithScope, BsonWriter::writeJavaScriptWithScope)
            .bind(BsonTokenTypes.DECIMAL_128, BsonType.DECIMAL128, BsonReader::readDecimal128, BsonWriter::writeDecimal128)
            .bind(BsonTokenTypes.MIN_KEY, BsonType.MIN_KEY, BsonReader::readMinKey, BsonWriter::writeMinKey)
            .bind(BsonTokenTypes.MAX_KEY, BsonType.MAX_KEY, BsonReader::readMaxKey, BsonWriter::writeMaxKey)
    );

    static final BsonUtils INSTANCE = new BsonUtils();

    private BsonUtils() {
    }

    @Override
    public String idPropertyName() {
        return "_id";
    }

    @Override
    public Class<BsonValue> rawType() {
        return BsonValue.class;
    }

    @Override
    public Reader newReader(BsonValue raw) {
        if (raw instanceof BsonDocument) {
            return new StandardBsonReaderAdapter(new BsonDocumentReader((BsonDocument) raw));
        }

        BsonDocument document = new BsonDocument("", raw);

        BsonDocumentReader reader = new BsonDocumentReader(document);
        reader.readStartDocument();
        reader.readName();

        return new StandardBsonReaderAdapter(reader);
    }

    @Override
    public MapLike<BsonValue> createMap(TypeAdapters typeAdapters, AnnotatedType mapType) {
        TypeRef<Map<?, ?>> typeRef = TypeRef.of(mapType);
        TypeAdapter<Map<?, ?>> mapAdapter = typeAdapters.getOrThrow(typeRef);

        return new MapLike<>() {
            private final BsonDocument document = new BsonDocument();

            @Override
            public void put(String key, @Nullable BsonValue value) {
                document.put(key, value != null ? value : BsonNull.VALUE);
            }

            @Override
            public void putAll(Map<?, ?> map, WriteContext ctx) throws IOException {
                BsonDocument result = new BsonDocument();

                try (BsonDocumentWriter writer = new BsonDocumentWriter(result)) {
                    mapAdapter.write(new BsonWriterAdapter(writer), typeRef, map, ctx);
                }

                document.putAll(result);
            }

            @Override
            public @Nullable Map<?, ?> asMap(@Nullable Map<?, ?> instance, ReadContext ctx) throws IOException {
                try (BsonDocumentReader reader = new BsonDocumentReader(document)) {
                    Reader readerAdapter = new StandardBsonReaderAdapter(reader);

                    if (!(mapAdapter instanceof TypeAdapter.Mutable)) {
                        return mapAdapter.read(readerAdapter, typeRef, ctx);
                    }

                    return ((TypeAdapter.Mutable<Map<?, ?>>) mapAdapter).mutate(readerAdapter, typeRef, instance, ctx);
                }
            }

            @Override
            public Map<String, BsonValue> asRawMap() {
                return Collections.unmodifiableMap(document);
            }
        };
    }
}
