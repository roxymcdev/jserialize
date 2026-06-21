package net.roxymc.jserialize.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;

public final class RecordUtils {
    private static final RecordSupport HANDLE = new RecordSupportImpl();

    private RecordUtils() {
    }

    public static boolean isRecord(Class<?> clazz) {
        return HANDLE.isRecord(clazz);
    }

    public static boolean isPrimaryConstructor(Constructor<?> constructor) {
        return HANDLE.isPrimaryConstructor(constructor);
    }

    public static AnnotatedType[] getComponentTypes(Class<?> clazz) {
        return HANDLE.getComponentTypes(clazz);
    }

    interface RecordSupport {
        boolean isRecord(Class<?> clazz);

        boolean isPrimaryConstructor(Constructor<?> constructor);

        AnnotatedType[] getComponentTypes(Class<?> clazz);
    }
}
