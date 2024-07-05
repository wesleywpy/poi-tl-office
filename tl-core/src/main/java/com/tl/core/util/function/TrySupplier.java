package com.tl.core.util.function;

@FunctionalInterface
public interface TrySupplier<T, E extends Throwable> {
    /**
     * get
     *
     * @return T
     * @author Wesley
     * @since 2022/11/16
     **/
    T get() throws E;
}
