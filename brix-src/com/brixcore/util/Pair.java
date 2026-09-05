package com.brixcore.util;

import com.android.tools.r8.RecordTag;
import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public final class Pair<K, V> extends RecordTag {
    private final K key;
    private final V value;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair) obj;
        return Objects.equals(this.key, pair.key) && Objects.equals(this.value, pair.value);
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public final int hashCode() {
        return Pair$$ExternalSyntheticRecord0.m(this.key, this.value);
    }

    public K key() {
        return this.key;
    }

    public V value() {
        return this.value;
    }

    public static <K, V> Pair<K, V> pair(K key, V value) {
        return new Pair<>(key, value);
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public String toString() {
        return "(" + this.key + ", " + this.value + ")";
    }
}
