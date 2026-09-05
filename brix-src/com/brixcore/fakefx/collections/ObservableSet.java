package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.Observable;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableSet<E> extends Set<E>, Observable {
    void addListener(SetChangeListener<? super E> setChangeListener);

    void removeListener(SetChangeListener<? super E> setChangeListener);
}
