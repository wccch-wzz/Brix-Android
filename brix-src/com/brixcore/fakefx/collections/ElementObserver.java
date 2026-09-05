package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.util.Callback;
import java.util.IdentityHashMap;

/* JADX INFO: loaded from: classes3.dex */
final class ElementObserver<E> {
    private IdentityHashMap<E, ElementsMapElement> elementsMap = new IdentityHashMap<>();
    private Callback<E, Observable[]> extractor;
    private final ObservableListBase<E> list;
    private final Callback<E, InvalidationListener> listenerGenerator;

    private static class ElementsMapElement {
        int counter = 1;
        InvalidationListener listener;

        public ElementsMapElement(InvalidationListener listener) {
            this.listener = listener;
        }

        public void increment() {
            this.counter++;
        }

        public int decrement() {
            int i = this.counter - 1;
            this.counter = i;
            return i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public InvalidationListener getListener() {
            return this.listener;
        }
    }

    ElementObserver(Callback<E, Observable[]> extractor, Callback<E, InvalidationListener> listenerGenerator, ObservableListBase<E> list) {
        this.extractor = extractor;
        this.listenerGenerator = listenerGenerator;
        this.list = list;
    }

    void attachListener(E e) {
        if (this.elementsMap != null && e != null) {
            if (this.elementsMap.containsKey(e)) {
                this.elementsMap.get(e).increment();
                return;
            }
            InvalidationListener listener = this.listenerGenerator.call(e);
            for (Observable o : this.extractor.call(e)) {
                o.addListener(listener);
            }
            this.elementsMap.put(e, new ElementsMapElement(listener));
        }
    }

    void detachListener(E e) {
        if (this.elementsMap != null && e != null) {
            ElementsMapElement el = this.elementsMap.get(e);
            for (Observable o : this.extractor.call(e)) {
                o.removeListener(el.getListener());
            }
            if (el.decrement() == 0) {
                this.elementsMap.remove(e);
            }
        }
    }
}
