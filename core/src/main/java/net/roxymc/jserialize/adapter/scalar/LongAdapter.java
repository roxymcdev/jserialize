package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

public final class LongAdapter extends AbstractNumberAdapter<Long> {
    public static final LongAdapter PRIMITIVE = new LongAdapter(long.class);
    public static final LongAdapter BOXED = new LongAdapter(Long.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private LongAdapter(Class<Long> type) {
        super(type);
    }

    public static Factory factory() {
        return FACTORY;
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
