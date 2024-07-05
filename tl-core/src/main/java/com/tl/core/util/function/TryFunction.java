package com.tl.core.util.function;

@FunctionalInterface
public interface TryFunction<T, R, E extends Throwable> {

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @param <E> Thrown exception.
     * @return a function that always returns its input argument
     */
    static <T, E extends Throwable> TryFunction<T, T, E> identity() {
        return t -> t;
    }

    /**
     * apply
     *
     * @param t 入参
     * @return R 函数返回值
     * @author Wesley
     * @since 2022/11/16
     **/
    R apply(T t) throws E;
}