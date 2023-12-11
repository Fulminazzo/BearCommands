package it.angrybear.managers;

import it.angrybear.interfaces.functions.QuadFunction;
import it.angrybear.interfaces.functions.TriFunction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An abstract Manager class. Allows creating custom managers.
 *
 * @param <T> the type of the object
 */
@Getter
public abstract class Manager<T> {
    protected final List<T> objects;

    public Manager() {
        this.objects = new ArrayList<>();
        reload();
    }

    /**
     * Reloads all the objects.
     */
    public void reload() {

    }

    /**
     * Add.
     *
     * @param t the object
     */
    public void add(T t) {
        if (t != null) this.objects.add(t);
    }

    /**
     * Remove.
     *
     * @param function to identify the object
     */
    protected void remove(Function<T, Boolean> function) {
        remove(get(function));
    }

    /**
     * Remove.
     *
     * @param <F>      the first parameter type
     * @param function to identify the object
     * @param f        the first parameter 
     */
    protected <F> void remove(BiFunction<T, F, Boolean> function, F f) {
        remove(get(function, f));
    }

    /**
     * Remove.
     *
     * @param <F>      the first parameter type
     * @param <S>      the second parameter type
     * @param function to identify the object
     * @param f        the first parameter 
     * @param s        the second parameter
     */
    protected <F, S> void remove(TriFunction<T, F, S, Boolean> function, F f, S s) {
        remove(get(function, f, s));
    }

    /**
     * Remove.
     *
     * @param <F>      the first parameter type
     * @param <S>      the second parameter type
     * @param <R>      the third parameter type
     * @param function to identify the object
     * @param f        the first parameter
     * @param s        the second parameter
     * @param r        the third parameter
     */
    protected <F, S, R> void remove(QuadFunction<T, F, S, R, Boolean> function, F f, S s, R r) {
        remove(get(function, f, s, r));
    }

    /**
     * Remove.
     *
     * @param t the object to remove
     */
    public void remove(T t) {
        if (t != null) this.objects.removeIf(o -> Objects.equals(t, o));
    }

    /**
     * Get an object.
     *
     * @param function to identify the object
     * @return the object if found, or null
     */
    protected T get(Function<T, Boolean> function) {
        if (function == null) return null;
        return objects.stream().filter(function::apply).findFirst().orElse(null);
    }

    /**
     * Get an object.
     *
     * @param <F>      the first parameter type
     * @param function to identify the object
     * @param f        the first parameter
     * @return the object if found, or null
     */
    protected <F> T get(BiFunction<T, F, Boolean> function, F f) {
        return objects.stream().filter(t -> function.apply(t, f)).findFirst().orElse(null);
    }

    /**
     * Get an object.
     *
     * @param <F>      the first parameter type
     * @param <S>      the second parameter type
     * @param function to identify the object
     * @param f        the first parameter
     * @param s        the second parameter
     * @return the object if found, or null
     */
    protected <F, S> T get(TriFunction<T, F, S, Boolean> function, F f, S s) {
        return objects.stream().filter(t -> function.apply(t, f, s)).findFirst().orElse(null);
    }

    /**
     * Get an object.
     *
     * @param <F>      the first parameter type
     * @param <S>      the second parameter type
     * @param <R>      the third parameter type
     * @param function to identify the object
     * @param f        the first parameter
     * @param s        the second parameter
     * @param r        the third parameter
     * @return the object if found, or null
     */
    protected <F, S, R> T get(QuadFunction<T, F, S, R, Boolean> function, F f, S s, R r) {
        return objects.stream().filter(t -> function.apply(t, f, s, r)).findFirst().orElse(null);
    }

    /**
     * Returns a filtered stream from the objects.
     *
     * @param predicate the predicate to filter the objects
     * @return the stream
     */
    public Stream<T> filter(Predicate<? super T> predicate) {
        return objects.stream().filter(predicate);
    }

    /**
     * Returns a mapped stream from the objects.
     *
     * @param <R>      a type parameter
     * @param function to identify the object
     * @return the stream
     */
    public <R> Stream<R> map(Function<T, R> function) {
        return objects.stream().map(function);
    }

    /**
     * Executes a consumer for each object in objects.
     *
     * @param action the action
     */
    public void forEach(Consumer<T> action) {
        objects.forEach(action);
    }
}