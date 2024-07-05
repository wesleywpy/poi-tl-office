package com.tl.core.util.function;

import com.qz.lims.report.framework.function.TryConsumer;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Try
 *
 * @author Wesley
 * @since 2022/11/16 09:41
 */
public interface Try<T> {

    /**
     * of
     *
     * @param t 容器值
     * @return com.qz.lims.report.framework.function.monad.Try<T>
     * @author Wesley
     * @since 2022/12/09
     **/
    static <T> Try<T> of(T t) {
        return Objects.isNull(t) ? Try.failure(new NullPointerException(" 容器值为Null ")) : new Success<>(t);
    }

    /**
     * Factory method for failure.
     *
     * @param e   抛出的Throwable
     * @param <T> 返回Type
     * @return a new Failure
     */
    static <T> Try<T> failure(Throwable e) {
        return new Failure<>(e);
    }

    /**
     * ofFailed
     *
     * @param f {@link TrySupplier}
     * @return com.qz.lims.report.framework.function.monad.Try<U>
     * @author Wesley
     * @since 2022/11/16
     **/
    static <T, E extends Throwable> Try<T> ofFailed(TrySupplier<T, E> f) {
        Objects.requireNonNull(f);

        try {
            return Try.of(f.get());
        } catch (Throwable t) {
            return Try.failure(t);
        }
    }

    /**
     * failed
     *
     * @param func {@link TryFunction}
     * @param input 入参
     * @return com.qz.lims.report.framework.function.monad.Try<R>
     * @author Wesley
     * @since 2022/12/09
     **/
    static <T, R, E extends Throwable> Try<R> ofFailed(TryFunction<T, R, E> func, final T input) {
        Objects.requireNonNull(func);

        try {
            return Try.of(func.apply(input));
        } catch (Throwable t) {
            return Try.failure(t);
        }
    }

    /**
     * map
     * @param <R> 返回类型
     * @return com.qz.lims.report.framework.function.monad.Try<R>
     * @author Wesley
     * @since 2022/12/09
     **/
    <R, E extends Throwable> Try<R> map(TryFunction<? super T, ? extends R, E> func);

    /**
     *
     * @param <R> 返回类型
     * @return new composed Try
     * @author Wesley
     * @since 2022/12/09 15:44
     */
    <R, E extends Throwable> Try<R> flatMap(TryFunction<? super T, Try<R>, E> func);



    /**
     * Try applying f(t) on the case of failure.
     * 恢复操作
     * @param func function that takes throwable and returns result
     * @return a new Try in the case of failure, or the current Success.
     */
    <R, E extends Throwable> Try<R> recoverWith(TryFunction<? super Throwable, Try<R>, E> func);

    /**
     * Performs the provided action, when successful
     * @param action action to run
     * @return new composed Try
     * @throws E if the action throws an exception
     */
    <E extends Throwable> Try<T> successTry(TryConsumer<T, E> action) throws E;

    /**
     * Performs the provided action, when failed
     * @param action action to run
     * @return new composed Try
     * @throws E if the action throws an exception
     */
    <E extends Throwable> Try<T> failureTry(TryConsumer<Throwable, E> action) throws E;


    /**
     * If a Try is a Success and the predicate holds true, the Success is passed further.
     * Otherwise (Failure or predicate doesn't hold), pass Failure.
     *
     * @param pred predicate applied to the value held by Try
     * @return For Success, the same success if predicate holds true, otherwise Failure
     */
    Try<T> filter(Predicate<T> pred);

    /**
     * Return another try in the case of failure.
     * Like recoverWith but without exposing the exception.
     *
     * @param f return the value or the value from the new try.
     * @return new composed Try
     */
    <E extends Throwable> Try<T> orElseTry(TrySupplier<T, E> f);

    /**
     * peek
     *
     * @param action 函数操作
     * @return com.qz.lims.report.framework.function.monad.Try<T>
     * @author Wesley
     * @since 2023/01/31 15:45
     **/
    <E extends Throwable> Try<T> peek(TryConsumer<T, E> action);

    // ----------------- 以下为终止操作

    /**
     * 恢复
     *
     * @param f function to execute on successful result.
     * @return new composed Try
     */
    T recover(Function<? super Throwable, T> f);

    /**
     * Return a value in the case of a failure.
     * This is similar to recover but does not expose the exception type.
     *
     * @param value return the try's value or else the value specified.
     * @return new composed Try
     */
    T orElse(T value);

    /**
     * Gets the value T on Success or throws the cause of the failure.
     *
     * @return T
     */
    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    /**
     * Gets the value T on Success or throws the cause of the failure.
     *
     * @return T
     */
    T get() throws Throwable;

    /**
     * ifSuccess
     * 如果成功则执行
     * @author Wesley
     * @since 2022/12/09
     **/
    void ifSuccess(Consumer<T> consumer);

    /**
     * isSuccess
     * 是否成功
     * @return boolean
     * @author Wesley
     * @since 2023/01/10
     **/
    boolean isSuccess();

    /**
     * Try contents wrapped in Optional.
     *
     * @return Optional of T, if Success, Empty if Failure or null value
     */
    Optional<T> toOptional();
}
