package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.Observable;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableMap<K, V> extends Map<K, V>, Observable {
    void addListener(MapChangeListener<? super K, ? super V> mapChangeListener);

    void removeListener(MapChangeListener<? super K, ? super V> mapChangeListener);
}
