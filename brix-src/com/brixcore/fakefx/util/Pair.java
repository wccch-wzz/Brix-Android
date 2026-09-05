package com.brixcore.fakefx.util;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.util.io.NetworkUtils;
import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair(@NamedArg("key") K key, @NamedArg("value") V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return this.key + NetworkUtils.NAME_VALUE_SEPARATOR + this.value;
    }

    public int hashCode() {
        int hash = (7 * 31) + (this.key != null ? this.key.hashCode() : 0);
        return (hash * 31) + (this.value != null ? this.value.hashCode() : 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair) o;
        if (this.key == null ? pair.key == null : this.key.equals(pair.key)) {
            return this.value == null ? pair.value == null : this.value.equals(pair.value);
        }
        return false;
    }
}
