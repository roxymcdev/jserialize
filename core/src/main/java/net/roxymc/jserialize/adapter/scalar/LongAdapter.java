package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

final class LongAdapter extends AbstractNumberAdapter<Long> {
    LongAdapter() {
        super(Long.class);
    }

    @Override
    protected Long fromLong(long value) {
        return value;
    }

    @Override
    protected Long parse(String value) throws NumberFormatException {
        return Long.parseLong(value);
    }

    @Override
    protected void write(Writer writer, Long value) throws IOException {
        writer.writeLong(value);
    }
}
