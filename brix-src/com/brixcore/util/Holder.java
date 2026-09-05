package com.brixcore.util;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import java.util.Objects;

/* JADX INFO: loaded from: classes11.dex */
public final class Holder<T> implements InvalidationListener {
    public T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }

    @Override // com.brixcore.fakefx.beans.InvalidationListener
    public void invalidated(Observable observable) {
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Holder)) {
            return false;
        }
        return Objects.equals(this.value, ((Holder) obj).value);
    }

    public String toString() {
        return "Holder[" + this.value + "]";
    }
}
