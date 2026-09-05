package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableObjectValue;
import com.brixcore.fakefx.binding.StringFormatter;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import java.util.Locale;

/* JADX INFO: loaded from: classes16.dex */
public abstract class ObjectExpression<T> implements ObservableObjectValue<T> {
    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public T getValue2() {
        return get();
    }

    public static <T> ObjectExpression<T> objectExpression(final ObservableObjectValue<T> value) {
        if (value != null) {
            return value instanceof ObjectExpression ? (ObjectExpression) value : new ObjectBinding<T>() { // from class: com.brixcore.fakefx.beans.binding.ObjectExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
                protected T computeValue() {
                    return (T) value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableObjectValue<T>> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public BooleanBinding isEqualTo(ObservableObjectValue<?> other) {
        return Bindings.equal((ObservableObjectValue<?>) this, other);
    }

    public BooleanBinding isEqualTo(Object other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(ObservableObjectValue<?> other) {
        return Bindings.notEqual((ObservableObjectValue<?>) this, other);
    }

    public BooleanBinding isNotEqualTo(Object other) {
        return Bindings.notEqual(this, other);
    }

    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    public StringBinding asString(String format) {
        return (StringBinding) Bindings.format(format, this);
    }

    public StringBinding asString(Locale locale, String format) {
        return (StringBinding) Bindings.format(locale, format, this);
    }
}
