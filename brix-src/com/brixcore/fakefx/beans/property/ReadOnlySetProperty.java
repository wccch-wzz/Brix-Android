package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.binding.SetExpression;
import com.brixcore.fakefx.collections.ObservableSet;
import java.util.Set;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlySetProperty<E> extends SetExpression<E> implements ReadOnlyProperty<ObservableSet<E>> {
    public void bindContentBidirectional(ObservableSet<E> set) {
        Bindings.bindContentBidirectional(this, set);
    }

    public void unbindContentBidirectional(Object object) {
        Bindings.unbindContentBidirectional(this, object);
    }

    public void bindContent(ObservableSet<E> set) {
        Bindings.bindContent(this, set);
    }

    public void unbindContent(Object object) {
        Bindings.unbindContent(this, object);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Set)) {
            return false;
        }
        Set c = (Set) obj;
        if (c.size() != size()) {
            return false;
        }
        try {
            return containsAll(c);
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e2) {
            return false;
        }
    }

    @Override // java.util.Set, java.util.Collection
    public int hashCode() {
        int h = 0;
        for (E e : this) {
            if (e != null) {
                h += e.hashCode();
            }
        }
        return h;
    }

    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("ReadOnlySetProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }
}
