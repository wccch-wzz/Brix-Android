package com.brixcore.fakefx.beans.value;

import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.binding.FlatMappedBinding;
import com.brixcore.fakefx.binding.MappedBinding;
import com.brixcore.fakefx.binding.OrElseBinding;
import java.util.function.Function;

/* JADX INFO: loaded from: classes2.dex */
public interface ObservableValue<T> extends Observable {
    void addListener(ChangeListener<? super T> changeListener);

    /* JADX INFO: renamed from: getValue */
    T getValue2();

    void removeListener(ChangeListener<? super T> changeListener);

    default <U> ObservableValue<U> map(Function<? super T, ? extends U> mapper) {
        return new MappedBinding(this, mapper);
    }

    default ObservableValue<T> orElse(T constant) {
        return new OrElseBinding(this, constant);
    }

    default <U> ObservableValue<U> flatMap(Function<? super T, ? extends ObservableValue<? extends U>> mapper) {
        return new FlatMappedBinding(this, mapper);
    }
}
