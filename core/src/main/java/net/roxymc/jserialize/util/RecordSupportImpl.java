package net.roxymc.jserialize.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;

final class RecordSupportImpl implements RecordUtils.RecordSupport {
    @Override
    public boolean isRecord(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean isPrimaryConstructor(Constructor<?> constructor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AnnotatedType[] getComponentTypes(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }
}
