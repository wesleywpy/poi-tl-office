package com.tl.core.util.function;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Success
 *
 * @author WangPanYong
 * @since 2022/12/09 09:19
 */
public class Success<T> implements Try<T> {
    private final T value;

    public Success(T value) {
        this.value = value;
    }

    @Override
    public <R, E extends Throwable> Try<R> map(TryFunction<? super T, ? extends R, E> func) {
        Objects.requireNonNull(func);
        try {
            return Try.of(func.apply(value));
        } catch (Throwable e) {
            return Try.failure(e);
        }
    }

    @Override
    public <R, E extends Throwable> Try<R> flatMap(TryFunction<? super T, Try<R>, E> func) {
        Objects.requireNonNull(func);
        try {
            return func.apply(value);
        } catch (Throwable t) {
            return Try.failure(t);
        }
    }

    @Override
    public T recover(Function<? super Throwable, T> f) {
        return value;
    }


    @Override
    public <R, E extends Throwable> Try<R> recoverWith(TryFunction<? super Throwable, Try<R>, E> func) {
        return (Try<R>) this;
    }

    @Override
    public T orElse(T value) {
        return this.value;
    }

    @Override
    public <E extends Throwable> Try<T> orElseTry(TrySupplier<T, E> f) {
        return this;
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value;
    }

    @Override
    public <E extends Throwable> Try<T> peek(TryConsumer<T, E> action) {
        try {
            action.accept(value);
            return this;
        } catch (Throwable t) {
            return Try.failure(t);
        }
    }

    @Override
    public T get() throws Throwable {
        return value;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public <E extends Throwable> Try<T> successTry(TryConsumer<T, E> action) throws E {
        action.accept(value);
        return this;
    }

    @Override
    public <E extends Throwable> Try<T> failureTry(TryConsumer<Throwable, E> action) throws E {
        return this;
    }

    @Override
    public Try<T> filter(Predicate<T> pred) {
        Objects.requireNonNull(pred);

        if (pred.test(value)) {
            return this;
        } else {
            return Try.failure(new NoSuchElementException("Predicate does not match for " + value));
        }
    }

    @Override
    public void ifSuccess(Consumer<T> consumer) {
        consumer.accept(this.value);
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }
}
