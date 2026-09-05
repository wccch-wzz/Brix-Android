package com.brixcore.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes11.dex */
public final class SimpleMultimap<K, V, M extends Collection<V>> {
    private final Map<K, M> map;
    private final Supplier<M> valuer;

    public SimpleMultimap(Supplier<Map<K, M>> mapper, Supplier<M> valuer) {
        this.map = mapper.get();
        this.valuer = valuer;
    }

    public int size() {
        return values().size();
    }

    public Set<K> keys() {
        return this.map.keySet();
    }

    public Collection<V> values() {
        Collection<V> res = this.valuer.get();
        for (Map.Entry<K, M> entry : this.map.entrySet()) {
            res.addAll(entry.getValue());
        }
        return res;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(K key) {
        return this.map.containsKey(key) && !this.map.get(key).isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Collection lambda$get$0(Object any) {
        return this.valuer.get();
    }

    public M get(K key) {
        return this.map.computeIfAbsent(key, new Function() { // from class: com.brixcore.util.SimpleMultimap$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$get$0(obj);
            }
        });
    }

    public void put(K key, V value) {
        get(key).add(value);
    }

    public void putAll(K key, Collection<? extends V> value) {
        get(key).addAll(value);
    }

    public M removeKey(K key) {
        return this.map.remove(key);
    }

    public boolean removeValue(V value) {
        boolean flag = false;
        for (M c : this.map.values()) {
            flag |= c.remove(value);
        }
        return flag;
    }

    public boolean removeValue(K key, V value) {
        return get(key).remove(value);
    }

    public void clear() {
        this.map.clear();
    }

    public void clear(K k) {
        if (this.map.containsKey(k)) {
            this.map.get(k).clear();
        } else {
            this.map.put(k, this.valuer.get());
        }
    }
}
