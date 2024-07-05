package com.tl.core.util.function;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Failure
 *
 * @author WangPanYong
 * @since 2022/12/09 09:20
 */
public class Failure<T> implements Try<T> {
    private final Throwable e;

    Failure(Throwable e) {
        this.e = e;
    }

    @Override
    public <R, E extends Throwable> Try<R> map(TryFunction<? super T, ? extends R, E> func) {
        return Try.failure(e);
    }

    @Override
    public <R, E extends Throwable> Try<R> flatMap(TryFunction<? super T, Try<R>, E> func) {
        return Try.failure(e);
    }

    @Override
    public T recover(Function<? super Throwable, T> f) {
        Objects.requireNonNull(f);
        return f.apply(e);
    }


    @Override
    public <R, E extends Throwable> Try<R> recoverWith(TryFunction<? super Throwable, Try<R>, E> func) {
        Objects.requireNonNull(func);
        try {
            return func.apply(e);
        } catch (Throwable throwable) {
            return Try.failure(throwable);
        }
    }

    @Override
    public T orElse(T value) {
        return value;
    }

    @Override
    public <E extends Throwable> Try<T> orElseTry(TrySupplier<T, E> func) {
        Objects.requireNonNull(func);
        return Try.ofFailed(func);
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        throw exceptionSupplier.get();
    }

    @Override
    public <E extends Throwable> Try<T> peek(TryConsumer<T, E> action) {
        return this;
    }

    @Override
    public T get() throws Throwable {
        throw e;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public <E extends Throwable> Try<T> successTry(TryConsumer<T, E> action) throws E {
        return this;
    }

    @Override
    public <E extends Throwable> Try<T> failureTry(TryConsumer<Throwable, E> action) throws E {
        action.accept(e);
        return this;
    }

    @Override
    public Try<T> filter(Predicate<T> pred) {
        return this;
    }

    @Override
    public void ifSuccess(Consumer<T> consumer) {

    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

}
