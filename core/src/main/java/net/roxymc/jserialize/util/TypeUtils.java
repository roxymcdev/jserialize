package net.roxymc.jserialize.util;

import net.roxymc.jserialize.type.TypeRef;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.getExactSuperType;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static boolean isEnum(TypeRef<?> type) {
        return isEnum(type.getRawType());
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public static AnnotatedParameterizedType resolveTypeParameters(AnnotatedType subtype, Class<?> supertype) {
        AnnotatedType type = getExactSuperType(capture(subtype), supertype);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(subtype.getType().getTypeName() + " must be a parameterized " + supertype.getTypeName());
        }

        return (AnnotatedParameterizedType) type;
    }
}
