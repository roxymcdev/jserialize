package net.roxymc.jserialize.token;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

abstract class AbstractScalarToken<T extends @Nullable Object> implements ScalarToken<T> {
    @Override
    public int hashCode() {
        return Objects.hashCode(value());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        ScalarToken<?> that = (ScalarToken<?>) obj;
        return Objects.equals(this.value(), that.value());
    }
}
