package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@ApiStatus.Internal
public abstract class AbstractValuedToken<T> implements ValuedToken<T> {
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

        ValuedToken<?> that = (ValuedToken<?>) obj;
        return Objects.equals(this.value(), that.value());
    }
}
