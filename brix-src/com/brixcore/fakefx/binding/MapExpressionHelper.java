package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableMapValue;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableMap;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: classes6.dex */
public abstract class MapExpressionHelper<K, V> extends ExpressionHelperBase {
    protected final ObservableMapValue<K, V> observable;

    protected abstract MapExpressionHelper<K, V> addListener(InvalidationListener invalidationListener);

    protected abstract MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> changeListener);

    protected abstract MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> mapChangeListener);

    protected abstract void fireValueChangedEvent();

    protected abstract void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change);

    protected abstract MapExpressionHelper<K, V> removeListener(InvalidationListener invalidationListener);

    protected abstract MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> changeListener);

    protected abstract MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> mapChangeListener);

    public static <K, V> MapExpressionHelper<K, V> addListener(MapExpressionHelper<K, V> helper, ObservableMapValue<K, V> observable, InvalidationListener listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        observable.getValue2();
        return helper == null ? new SingleInvalidation(observable, listener) : helper.addListener(listener);
    }

    public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> helper, InvalidationListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <K, V> MapExpressionHelper<K, V> addListener(MapExpressionHelper<K, V> helper, ObservableMapValue<K, V> observable, ChangeListener<? super ObservableMap<K, V>> listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        return helper == null ? new SingleChange(observable, listener) : helper.addListener(listener);
    }

    public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> helper, ChangeListener<? super ObservableMap<K, V>> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <K, V> MapExpressionHelper<K, V> addListener(MapExpressionHelper<K, V> helper, ObservableMapValue<K, V> observable, MapChangeListener<? super K, ? super V> listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        return helper == null ? new SingleMapChange(observable, listener) : helper.addListener(listener);
    }

    public static <K, V> MapExpressionHelper<K, V> removeListener(MapExpressionHelper<K, V> helper, MapChangeListener<? super K, ? super V> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <K, V> void fireValueChangedEvent(MapExpressionHelper<K, V> helper) {
        if (helper != null) {
            helper.fireValueChangedEvent();
        }
    }

    public static <K, V> void fireValueChangedEvent(MapExpressionHelper<K, V> helper, MapChangeListener.Change<? extends K, ? extends V> change) {
        if (helper != null) {
            helper.fireValueChangedEvent(change);
        }
    }

    protected MapExpressionHelper(ObservableMapValue<K, V> observable) {
        this.observable = observable;
    }

    private static class SingleInvalidation<K, V> extends MapExpressionHelper<K, V> {
        private final InvalidationListener listener;

        private SingleInvalidation(ObservableMapValue<K, V> observable, InvalidationListener listener) {
            super(observable);
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(InvalidationListener listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(InvalidationListener listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent() {
            this.listener.invalidated(this.observable);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            this.listener.invalidated(this.observable);
        }
    }

    private static class SingleChange<K, V> extends MapExpressionHelper<K, V> {
        private ObservableMap<K, V> currentValue;
        private final ChangeListener<? super ObservableMap<K, V>> listener;

        private SingleChange(ObservableMapValue<K, V> observable, ChangeListener<? super ObservableMap<K, V>> listener) {
            super(observable);
            this.listener = listener;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(InvalidationListener listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent() {
            ObservableMap<K, V> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            if (this.currentValue != oldValue) {
                this.listener.changed(this.observable, oldValue, this.currentValue);
            }
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            this.listener.changed(this.observable, this.currentValue, this.currentValue);
        }
    }

    private static class SingleMapChange<K, V> extends MapExpressionHelper<K, V> {
        private ObservableMap<K, V> currentValue;
        private final MapChangeListener<? super K, ? super V> listener;

        private SingleMapChange(ObservableMapValue<K, V> observable, MapChangeListener<? super K, ? super V> listener) {
            super(observable);
            this.listener = listener;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(InvalidationListener listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent() {
            ObservableMap<K, V> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            if (this.currentValue != oldValue) {
                SimpleChange<K, V> change = new SimpleChange<>(this.observable);
                if (this.currentValue == null) {
                    for (Map.Entry<K, V> element : oldValue.entrySet()) {
                        this.listener.onChanged(change.setRemoved(element.getKey(), element.getValue()));
                    }
                    return;
                }
                if (oldValue == null) {
                    for (Map.Entry<K, V> element2 : this.currentValue.entrySet()) {
                        this.listener.onChanged(change.setAdded(element2.getKey(), element2.getValue()));
                    }
                    return;
                }
                for (Map.Entry<K, V> element3 : oldValue.entrySet()) {
                    K key = element3.getKey();
                    V oldEntry = element3.getValue();
                    if (this.currentValue.containsKey(key)) {
                        V newEntry = this.currentValue.get(key);
                        if (oldEntry == null) {
                            if (newEntry != null) {
                                this.listener.onChanged(change.setPut(key, oldEntry, newEntry));
                            }
                        } else if (!newEntry.equals(oldEntry)) {
                            this.listener.onChanged(change.setPut(key, oldEntry, newEntry));
                        }
                    } else {
                        this.listener.onChanged(change.setRemoved(key, oldEntry));
                    }
                }
                for (Map.Entry<K, V> element4 : this.currentValue.entrySet()) {
                    K key2 = element4.getKey();
                    if (!oldValue.containsKey(key2)) {
                        this.listener.onChanged(change.setAdded(key2, element4.getValue()));
                    }
                }
            }
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            this.listener.onChanged(new SimpleChange(this.observable, change));
        }
    }

    private static class Generic<K, V> extends MapExpressionHelper<K, V> {
        private ChangeListener<? super ObservableMap<K, V>>[] changeListeners;
        private int changeSize;
        private ObservableMap<K, V> currentValue;
        private InvalidationListener[] invalidationListeners;
        private int invalidationSize;
        private boolean locked;
        private MapChangeListener<? super K, ? super V>[] mapChangeListeners;
        private int mapChangeSize;

        private Generic(ObservableMapValue<K, V> observable, InvalidationListener listener0, InvalidationListener listener1) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{listener0, listener1};
            this.invalidationSize = 2;
        }

        private Generic(ObservableMapValue<K, V> observable, ChangeListener<? super ObservableMap<K, V>> listener0, ChangeListener<? super ObservableMap<K, V>> listener1) {
            super(observable);
            this.changeListeners = new ChangeListener[]{listener0, listener1};
            this.changeSize = 2;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableMapValue<K, V> observable, MapChangeListener<? super K, ? super V> listener0, MapChangeListener<? super K, ? super V> listener1) {
            super(observable);
            this.mapChangeListeners = new MapChangeListener[]{listener0, listener1};
            this.mapChangeSize = 2;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableMapValue<K, V> observable, InvalidationListener invalidationListener, ChangeListener<? super ObservableMap<K, V>> changeListener) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.changeListeners = new ChangeListener[]{changeListener};
            this.changeSize = 1;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableMapValue<K, V> observable, InvalidationListener invalidationListener, MapChangeListener<? super K, ? super V> listChangeListener) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.mapChangeListeners = new MapChangeListener[]{listChangeListener};
            this.mapChangeSize = 1;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableMapValue<K, V> observable, ChangeListener<? super ObservableMap<K, V>> changeListener, MapChangeListener<? super K, ? super V> listChangeListener) {
            super(observable);
            this.changeListeners = new ChangeListener[]{changeListener};
            this.changeSize = 1;
            this.mapChangeListeners = new MapChangeListener[]{listChangeListener};
            this.mapChangeSize = 1;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(InvalidationListener listener) {
            if (this.invalidationListeners == null) {
                this.invalidationListeners = new InvalidationListener[]{listener};
                this.invalidationSize = 1;
            } else {
                int oldCapacity = this.invalidationListeners.length;
                if (this.locked) {
                    int newCapacity = this.invalidationSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.invalidationListeners = (InvalidationListener[]) Arrays.copyOf(this.invalidationListeners, newCapacity);
                } else if (this.invalidationSize == oldCapacity) {
                    this.invalidationSize = trim(this.invalidationSize, this.invalidationListeners);
                    if (this.invalidationSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.invalidationListeners = (InvalidationListener[]) Arrays.copyOf(this.invalidationListeners, newCapacity2);
                    }
                }
                InvalidationListener[] invalidationListenerArr = this.invalidationListeners;
                int i = this.invalidationSize;
                this.invalidationSize = i + 1;
                invalidationListenerArr[i] = listener;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(InvalidationListener listener) {
            if (this.invalidationListeners != null) {
                for (int index = 0; index < this.invalidationSize; index++) {
                    if (listener.equals(this.invalidationListeners[index])) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.mapChangeSize == 0) {
                                return new SingleChange(this.observable, this.changeListeners[0]);
                            }
                            if (this.changeSize == 0 && this.mapChangeSize == 1) {
                                return new SingleMapChange(this.observable, this.mapChangeListeners[0]);
                            }
                            this.invalidationListeners = null;
                            this.invalidationSize = 0;
                            break;
                        }
                        if (this.invalidationSize == 2 && this.changeSize == 0 && this.mapChangeSize == 0) {
                            return new SingleInvalidation(this.observable, this.invalidationListeners[1 - index]);
                        }
                        int numMoved = (this.invalidationSize - index) - 1;
                        InvalidationListener[] oldListeners = this.invalidationListeners;
                        if (this.locked) {
                            this.invalidationListeners = new InvalidationListener[this.invalidationListeners.length];
                            System.arraycopy(oldListeners, 0, this.invalidationListeners, 0, index + 1);
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.invalidationListeners, index, numMoved);
                        }
                        this.invalidationSize--;
                        if (!this.locked) {
                            this.invalidationListeners[this.invalidationSize] = null;
                            break;
                        }
                        break;
                    }
                }
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            if (this.changeListeners == null) {
                this.changeListeners = new ChangeListener[]{listener};
                this.changeSize = 1;
            } else {
                int oldCapacity = this.changeListeners.length;
                if (this.locked) {
                    int newCapacity = this.changeSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.changeListeners = (ChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity);
                } else if (this.changeSize == oldCapacity) {
                    this.changeSize = trim(this.changeSize, this.changeListeners);
                    if (this.changeSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.changeListeners = (ChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity2);
                    }
                }
                ChangeListener<? super ObservableMap<K, V>>[] changeListenerArr = this.changeListeners;
                int i = this.changeSize;
                this.changeSize = i + 1;
                changeListenerArr[i] = listener;
            }
            if (this.changeSize == 1) {
                this.currentValue = this.observable.getValue2();
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
            if (this.changeListeners != null) {
                for (int index = 0; index < this.changeSize; index++) {
                    if (listener.equals(this.changeListeners[index])) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.mapChangeSize == 0) {
                                return new SingleInvalidation(this.observable, this.invalidationListeners[0]);
                            }
                            if (this.invalidationSize == 0 && this.mapChangeSize == 1) {
                                return new SingleMapChange(this.observable, this.mapChangeListeners[0]);
                            }
                            this.changeListeners = null;
                            this.changeSize = 0;
                            break;
                        }
                        if (this.changeSize == 2 && this.invalidationSize == 0 && this.mapChangeSize == 0) {
                            return new SingleChange(this.observable, this.changeListeners[1 - index]);
                        }
                        int numMoved = (this.changeSize - index) - 1;
                        ChangeListener<? super ObservableMap<K, V>>[] oldListeners = this.changeListeners;
                        if (this.locked) {
                            this.changeListeners = new ChangeListener[this.changeListeners.length];
                            System.arraycopy(oldListeners, 0, this.changeListeners, 0, index + 1);
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.changeListeners, index, numMoved);
                        }
                        this.changeSize--;
                        if (!this.locked) {
                            this.changeListeners[this.changeSize] = null;
                            break;
                        }
                        break;
                    }
                }
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            if (this.mapChangeListeners == null) {
                this.mapChangeListeners = new MapChangeListener[]{listener};
                this.mapChangeSize = 1;
            } else {
                int oldCapacity = this.mapChangeListeners.length;
                if (this.locked) {
                    int newCapacity = this.mapChangeSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.mapChangeListeners = (MapChangeListener[]) Arrays.copyOf(this.mapChangeListeners, newCapacity);
                } else if (this.mapChangeSize == oldCapacity) {
                    this.mapChangeSize = trim(this.mapChangeSize, this.mapChangeListeners);
                    if (this.mapChangeSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.mapChangeListeners = (MapChangeListener[]) Arrays.copyOf(this.mapChangeListeners, newCapacity2);
                    }
                }
                MapChangeListener<? super K, ? super V>[] mapChangeListenerArr = this.mapChangeListeners;
                int i = this.mapChangeSize;
                this.mapChangeSize = i + 1;
                mapChangeListenerArr[i] = listener;
            }
            if (this.mapChangeSize == 1) {
                this.currentValue = this.observable.getValue2();
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected MapExpressionHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            if (this.mapChangeListeners != null) {
                for (int index = 0; index < this.mapChangeSize; index++) {
                    if (listener.equals(this.mapChangeListeners[index])) {
                        if (this.mapChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return new SingleInvalidation(this.observable, this.invalidationListeners[0]);
                            }
                            if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return new SingleChange(this.observable, this.changeListeners[0]);
                            }
                            this.mapChangeListeners = null;
                            this.mapChangeSize = 0;
                            break;
                        }
                        if (this.mapChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return new SingleMapChange(this.observable, this.mapChangeListeners[1 - index]);
                        }
                        int numMoved = (this.mapChangeSize - index) - 1;
                        MapChangeListener<? super K, ? super V>[] oldListeners = this.mapChangeListeners;
                        if (this.locked) {
                            this.mapChangeListeners = new MapChangeListener[this.mapChangeListeners.length];
                            System.arraycopy(oldListeners, 0, this.mapChangeListeners, 0, index + 1);
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.mapChangeListeners, index, numMoved);
                        }
                        this.mapChangeSize--;
                        if (!this.locked) {
                            this.mapChangeListeners[this.mapChangeSize] = null;
                            break;
                        }
                        break;
                    }
                }
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent() throws Throwable {
            if (this.changeSize == 0 && this.mapChangeSize == 0) {
                notifyListeners(this.currentValue, null);
                return;
            }
            ObservableMap<K, V> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            notifyListeners(oldValue, null);
        }

        @Override // com.brixcore.fakefx.binding.MapExpressionHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) throws Throwable {
            SimpleChange<K, V> mappedChange = this.mapChangeSize == 0 ? null : new SimpleChange<>(this.observable, change);
            notifyListeners(this.currentValue, mappedChange);
        }

        /* JADX WARN: Code duplicated, block: B:52:0x00ed A[Catch: all -> 0x0138, LOOP:8: B:51:0x00eb->B:52:0x00ed, LOOP_END, TryCatch #1 {all -> 0x0138, blocks: (B:20:0x0050, B:22:0x0054, B:23:0x005c, B:25:0x0062, B:27:0x0076, B:31:0x0083, B:32:0x008d, B:34:0x0093, B:36:0x00a7, B:39:0x00b2, B:40:0x00ba, B:42:0x00c0, B:44:0x00d6, B:50:0x00e7, B:52:0x00ed, B:48:0x00e1, B:54:0x00f6, B:56:0x00fc, B:58:0x0105, B:59:0x010f, B:61:0x0115, B:63:0x0125, B:65:0x012f), top: B:77:0x0050 }] */
        private void notifyListeners(ObservableMap<K, V> observableMap, SimpleChange<K, V> simpleChange) throws Throwable {
            boolean z;
            int i;
            InvalidationListener[] invalidationListenerArr = this.invalidationListeners;
            int i2 = this.invalidationSize;
            ChangeListener<? super ObservableMap<K, V>>[] changeListenerArr = this.changeListeners;
            int i3 = this.changeSize;
            MapChangeListener<? super K, ? super V>[] mapChangeListenerArr = this.mapChangeListeners;
            int i4 = this.mapChangeSize;
            try {
                this.locked = true;
                for (int i5 = 0; i5 < i2; i5++) {
                    invalidationListenerArr[i5].invalidated(this.observable);
                }
                if (this.currentValue != observableMap || simpleChange != null) {
                    for (int i6 = 0; i6 < i3; i6++) {
                        changeListenerArr[i6].changed(this.observable, observableMap, this.currentValue);
                    }
                    if (i4 > 0) {
                        if (simpleChange != null) {
                            for (int i7 = 0; i7 < i4; i7++) {
                                mapChangeListenerArr[i7].onChanged(simpleChange);
                            }
                        } else {
                            SimpleChange simpleChange2 = new SimpleChange(this.observable);
                            try {
                                if (this.currentValue == null) {
                                    for (Map.Entry<K, V> entry : observableMap.entrySet()) {
                                        simpleChange2.setRemoved(entry.getKey(), entry.getValue());
                                        for (int i8 = 0; i8 < i4; i8++) {
                                            mapChangeListenerArr[i8].onChanged(simpleChange2);
                                        }
                                    }
                                } else if (observableMap == null) {
                                    for (Map.Entry<K, V> entry2 : this.currentValue.entrySet()) {
                                        simpleChange2.setAdded(entry2.getKey(), entry2.getValue());
                                        for (int i9 = 0; i9 < i4; i9++) {
                                            mapChangeListenerArr[i9].onChanged(simpleChange2);
                                        }
                                    }
                                } else {
                                    for (Map.Entry<K, V> entry3 : observableMap.entrySet()) {
                                        K key = entry3.getKey();
                                        V value = entry3.getValue();
                                        if (this.currentValue.containsKey(key)) {
                                            V v = this.currentValue.get(key);
                                            if (value == null) {
                                                if (v != null) {
                                                    simpleChange2.setPut(key, value, v);
                                                    for (i = 0; i < i4; i++) {
                                                        mapChangeListenerArr[i].onChanged(simpleChange2);
                                                    }
                                                }
                                            } else if (!v.equals(value)) {
                                                simpleChange2.setPut(key, value, v);
                                                while (i < i4) {
                                                    mapChangeListenerArr[i].onChanged(simpleChange2);
                                                }
                                            }
                                        } else {
                                            simpleChange2.setRemoved(key, value);
                                            for (int i10 = 0; i10 < i4; i10++) {
                                                mapChangeListenerArr[i10].onChanged(simpleChange2);
                                            }
                                        }
                                    }
                                    for (Map.Entry<K, V> entry4 : this.currentValue.entrySet()) {
                                        K key2 = entry4.getKey();
                                        if (!observableMap.containsKey(key2)) {
                                            simpleChange2.setAdded(key2, entry4.getValue());
                                            for (int i11 = 0; i11 < i4; i11++) {
                                                mapChangeListenerArr[i11].onChanged(simpleChange2);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                z = false;
                                this.locked = z;
                                throw th;
                            }
                        }
                    }
                }
                this.locked = false;
            } catch (Throwable th2) {
                th = th2;
                z = false;
            }
        }
    }

    public static class SimpleChange<K, V> extends MapChangeListener.Change<K, V> {
        private boolean addOp;
        private V added;
        private K key;
        private V old;
        private boolean removeOp;

        public SimpleChange(ObservableMap<K, V> set) {
            super(set);
        }

        public SimpleChange(ObservableMap<K, V> set, MapChangeListener.Change<? extends K, ? extends V> source) {
            super(set);
            this.key = source.getKey();
            this.old = source.getValueRemoved();
            this.added = source.getValueAdded();
            this.addOp = source.wasAdded();
            this.removeOp = source.wasRemoved();
        }

        public SimpleChange<K, V> setRemoved(K key, V old) {
            this.key = key;
            this.old = old;
            this.added = null;
            this.addOp = false;
            this.removeOp = true;
            return this;
        }

        public SimpleChange<K, V> setAdded(K key, V added) {
            this.key = key;
            this.old = null;
            this.added = added;
            this.addOp = true;
            this.removeOp = false;
            return this;
        }

        public SimpleChange<K, V> setPut(K key, V old, V added) {
            this.key = key;
            this.old = old;
            this.added = added;
            this.addOp = true;
            this.removeOp = true;
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public boolean wasAdded() {
            return this.addOp;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public boolean wasRemoved() {
            return this.removeOp;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public K getKey() {
            return this.key;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public V getValueAdded() {
            return this.added;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public V getValueRemoved() {
            return this.old;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.addOp) {
                if (this.removeOp) {
                    builder.append("replaced ").append(this.old).append(" by ").append(this.added);
                } else {
                    builder.append("added ").append(this.added);
                }
            } else {
                builder.append("removed ").append(this.old);
            }
            builder.append(" at key ").append(this.key);
            return builder.toString();
        }
    }
}
