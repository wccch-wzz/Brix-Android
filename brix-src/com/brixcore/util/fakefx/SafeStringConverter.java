package com.brixcore.util.fakefx;

import com.brixcore.fakefx.util.StringConverter;
import com.brixcore.util.function.ExceptionalFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes15.dex */
public class SafeStringConverter<S extends T, T> extends StringConverter<T> {
    private ExceptionalFunction<String, S, ?> converter;
    private Class<?> malformedExceptionClass;
    private S fallbackValue = null;
    private List<Predicate<S>> restrictions = new ArrayList();

    public static SafeStringConverter<Integer, Number> fromInteger() {
        return new SafeStringConverter(new ExceptionalFunction() { // from class: com.brixcore.util.fakefx.SafeStringConverter$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return Integer.valueOf(Integer.parseInt((String) obj));
            }
        }, NumberFormatException.class).fallbackTo(0);
    }

    public static SafeStringConverter<Double, Number> fromDouble() {
        return new SafeStringConverter(new SafeStringConverter$$ExternalSyntheticLambda1(), NumberFormatException.class).fallbackTo(Double.valueOf(0.0d));
    }

    public static SafeStringConverter<Double, Number> fromFiniteDouble() {
        return new SafeStringConverter(new SafeStringConverter$$ExternalSyntheticLambda1(), NumberFormatException.class).restrict(new Predicate() { // from class: com.brixcore.util.fakefx.SafeStringConverter$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Double.isFinite(((Double) obj).doubleValue());
            }
        }).fallbackTo(Double.valueOf(0.0d));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <E extends Exception> SafeStringConverter(ExceptionalFunction<String, S, E> exceptionalFunction, Class<E> cls) {
        this.converter = exceptionalFunction;
        this.malformedExceptionClass = cls;
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(T object) {
        return object == null ? "" : object.toString();
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public S fromString(String string) {
        return tryParse(string).orElse(this.fallbackValue);
    }

    private Optional<S> tryParse(String string) {
        if (string == null) {
            return Optional.empty();
        }
        try {
            S converted = this.converter.apply(string);
            if (!filter(converted)) {
                return Optional.empty();
            }
            return Optional.of(converted);
        } catch (Exception e) {
            if (this.malformedExceptionClass.isInstance(e)) {
                return Optional.empty();
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new RuntimeException(e);
        }
    }

    protected boolean filter(S value) {
        for (Predicate<S> restriction : this.restrictions) {
            if (!restriction.test(value)) {
                return false;
            }
        }
        return true;
    }

    public SafeStringConverter<S, T> fallbackTo(S fallbackValue) {
        this.fallbackValue = fallbackValue;
        return this;
    }

    public SafeStringConverter<S, T> restrict(Predicate<S> condition) {
        this.restrictions.add(condition);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$asPredicate$0(String string) {
        return tryParse(string).isPresent();
    }

    public Predicate<String> asPredicate() {
        return new Predicate() { // from class: com.brixcore.util.fakefx.SafeStringConverter$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$asPredicate$0((String) obj);
            }
        };
    }

    public SafeStringConverter<S, T> asPredicate(Consumer<Predicate<String>> consumer) {
        consumer.accept(asPredicate());
        return this;
    }
}
