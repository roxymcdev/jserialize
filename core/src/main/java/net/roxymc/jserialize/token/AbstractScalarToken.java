package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class AbstractScalarToken<T> extends AbstractValuedToken<T> implements ScalarToken<T> {
}
