package net.roxymc.jserialize.util;

import io.leangen.geantyref.AnnotatedCaptureType;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeVisitor;
import net.roxymc.jserialize.model.property.MethodRef;
import net.roxymc.jserialize.type.TypeRef;

import java.lang.reflect.*;

import static io.leangen.geantyref.GenericTypeReflector.*;
import static java.lang.String.format;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static boolean isEnum(TypeRef<?> type) {
        return isEnum(type.getRawType());
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public static AnnotatedParameterizedType resolveTypeParams(TypeRef<?> type, Class<?> supertype) {
        return resolveTypeParams(type.getAnnotatedType(), supertype);
    }

    public static AnnotatedParameterizedType resolveTypeParams(AnnotatedType subtype, Class<?> supertype) {
        AnnotatedType type = getExactSuperType(capture(subtype), supertype);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(subtype.getType().getTypeName() + " must be a parameterized " + supertype.getTypeName());
        }

        return (AnnotatedParameterizedType) type;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AnnotatedType> T resolveUpperBound(T type) {
        return (T) transform(type, new TypeVisitor() {
            @Override
            protected AnnotatedType visitVariable(final AnnotatedTypeVariable type) {
                return updateAnnotations(transform(type.getAnnotatedBounds()[0], this), type.getAnnotations());
            }

            @Override
            protected AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
                checkLowerBounds(type.getAnnotatedLowerBounds());

                return updateAnnotations(transform(type.getAnnotatedUpperBounds()[0], this), type.getAnnotations());
            }

            @Override
            protected AnnotatedType visitCaptureType(AnnotatedCaptureType type) {
                checkLowerBounds(type.getAnnotatedLowerBounds());

                AnnotatedType bound = type.getAnnotatedUpperBounds()[0];

                if (bound instanceof AnnotatedParameterizedType) {
                    AnnotatedType[] typeArgs = ((AnnotatedParameterizedType) bound).getAnnotatedActualTypeArguments();

                    for (AnnotatedType typeArg : typeArgs) {
                        if (!type.equals(typeArg)) {
                            continue;
                        }

                        return annotate(erase(bound.getType()), GenericTypeReflector.merge(type.getAnnotations(), bound.getAnnotations()));
                    }
                }

                return updateAnnotations(transform(bound, this), type.getAnnotations());
            }

            private void checkLowerBounds(AnnotatedType[] lowerBounds) {
                if (lowerBounds.length == 0) {
                    return;
                }

                throw new IllegalArgumentException(format(
                        "Cannot deserialize lower-bounded wildcard/capture ? super %1$s — use a concrete type %1$s or ? extends %1$s instead",
                        GenericTypeReflector.getTypeName(lowerBounds[0].getType())
                ));
            }
        });
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, TypeRef<?> typeAndParams) {
        return resolveDirectType(unresolved, typeAndParams.getAnnotatedType());
    }

    public static AnnotatedType resolveDirectType(MethodRef unresolved, TypeRef<?> typeAndParams) {
        AnnotatedType declaringType = getExactSuperType(capture(typeAndParams.getAnnotatedType()), unresolved.declaringClass());

        return resolveDirectType(unresolved.valueType(), declaringType);
    }

    public static AnnotatedType resolveDirectType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
        if (unresolved instanceof AnnotatedWildcardType || unresolved instanceof AnnotatedCaptureType) {
            return resolveDirectType(resolveUpperBound(unresolved), typeAndParams);
        } else if (unresolved instanceof AnnotatedTypeVariable) {
            @SuppressWarnings("unchecked")
            TypeVariable<? extends Class<?>> typeVar = (TypeVariable<? extends Class<?>>) unresolved.getType();

            AnnotatedType type = GenericTypeReflector.getTypeParameter(typeAndParams, typeVar);
            if (type == null) {
                return unresolved;
            }

            return resolveDirectType(updateAnnotations(type, unresolved.getAnnotations()), typeAndParams);
        } else {
            return GenericTypeReflector.resolveType(unresolved, typeAndParams);
        }
    }
}
