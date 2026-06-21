package net.roxymc.jserialize.util;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeToken;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean isPrimitive(TypeToken<?> type) {
        return isPrimitive(type.getRawType());
    }

    public static boolean isPrimitive(Class<?> type) {
        return !Object.class.isAssignableFrom(type);
    }

    public static AnnotatedType box(AnnotatedType type) {
        Type rawType = type.getType();
        Type boxedType = GenericTypeReflector.box(rawType);

        if (rawType == boxedType) {
            return type;
        }

        return GenericTypeReflector.annotate(boxedType, type.getAnnotations());
    }
}
