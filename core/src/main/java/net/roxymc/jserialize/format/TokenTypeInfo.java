package net.roxymc.jserialize.format;

import net.roxymc.jserialize.util.IOBiConsumer;
import net.roxymc.jserialize.util.IOConsumer;
import net.roxymc.jserialize.util.IOFunction;

import java.io.IOException;

public interface TokenTypeInfo<R, W> {
    final class NonValued<R, W> implements TokenTypeInfo<R, W> {
        private final IOConsumer<R> reader;
        private final IOConsumer<W> writer;

        NonValued(IOConsumer<R> reader, IOConsumer<W> writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public void read(R reader) throws IOException {
            this.reader.accept(reader);
        }

        public void write(W writer) throws IOException {
            this.writer.accept(writer);
        }
    }

    final class Valued<R, W, T> implements TokenTypeInfo<R, W> {
        private final IOFunction<R, T> reader;
        private final IOBiConsumer<W, T> writer;

        Valued(IOFunction<R, T> reader, IOBiConsumer<W, T> writer) {
            this.reader = reader;
            this.writer = writer;
        }

        @SuppressWarnings("DataFlowIssue") // IntelliJ has existential issues
        public T read(R reader) throws IOException {
            return this.reader.apply(reader);
        }

        public void write(W writer, T value) throws IOException {
            this.writer.accept(writer, value);
        }
    }
}
