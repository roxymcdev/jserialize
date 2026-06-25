package net.roxymc.jserialize.util;

import io.leangen.geantyref.GenericTypeReflector;

import java.lang.reflect.Type;

public final class TypeChecks {
    private TypeChecks() {
    }

    public static void checkAssignable(Type type, Type subtype) {
        if (!GenericTypeReflector.isSuperType(type, subtype)) {
            throw new IllegalStateException(subtype.getTypeName() + " is not a " + type.getTypeName());
        }
    }
}
