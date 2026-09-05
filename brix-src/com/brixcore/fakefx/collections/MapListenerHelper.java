package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.binding.ExpressionHelperBase;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public abstract class MapListenerHelper<K, V> extends ExpressionHelperBase {
    protected abstract MapListenerHelper<K, V> addListener(InvalidationListener invalidationListener);

    protected abstract MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> mapChangeListener);

    protected abstract void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change);

    protected abstract MapListenerHelper<K, V> removeListener(InvalidationListener invalidationListener);

    protected abstract MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> mapChangeListener);

    public static <K, V> MapListenerHelper<K, V> addListener(MapListenerHelper<K, V> helper, InvalidationListener listener) {
        if (listener != null) {
            return helper == null ? new SingleInvalidation(listener) : helper.addListener(listener);
        }
        throw new NullPointerException();
    }

    public static <K, V> MapListenerHelper<K, V> removeListener(MapListenerHelper<K, V> helper, InvalidationListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <K, V> MapListenerHelper<K, V> addListener(MapListenerHelper<K, V> helper, MapChangeListener<? super K, ? super V> listener) {
        if (listener != null) {
            return helper == null ? new SingleChange(listener) : helper.addListener(listener);
        }
        throw new NullPointerException();
    }

    public static <K, V> MapListenerHelper<K, V> removeListener(MapListenerHelper<K, V> helper, MapChangeListener<? super K, ? super V> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <K, V> void fireValueChangedEvent(MapListenerHelper<K, V> helper, MapChangeListener.Change<? extends K, ? extends V> change) {
        if (helper != null) {
            helper.fireValueChangedEvent(change);
        }
    }

    public static <K, V> boolean hasListeners(MapListenerHelper<K, V> helper) {
        return helper != null;
    }

    private static class SingleInvalidation<K, V> extends MapListenerHelper<K, V> {
        private final InvalidationListener listener;

        private SingleInvalidation(InvalidationListener listener) {
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> addListener(InvalidationListener listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(InvalidationListener listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            try {
                this.listener.invalidated(change.getMap());
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    private static class SingleChange<K, V> extends MapListenerHelper<K, V> {
        private final MapChangeListener<? super K, ? super V> listener;

        private SingleChange(MapChangeListener<? super K, ? super V> listener) {
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> addListener(InvalidationListener listener) {
            return new Generic(listener, this.listener);
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            try {
                this.listener.onChanged(change);
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    private static class Generic<K, V> extends MapListenerHelper<K, V> {
        private MapChangeListener<? super K, ? super V>[] changeListeners;
        private int changeSize;
        private InvalidationListener[] invalidationListeners;
        private int invalidationSize;
        private boolean locked;

        private Generic(InvalidationListener listener0, InvalidationListener listener1) {
            this.invalidationListeners = new InvalidationListener[]{listener0, listener1};
            this.invalidationSize = 2;
        }

        private Generic(MapChangeListener<? super K, ? super V> listener0, MapChangeListener<? super K, ? super V> listener1) {
            this.changeListeners = new MapChangeListener[]{listener0, listener1};
            this.changeSize = 2;
        }

        private Generic(InvalidationListener invalidationListener, MapChangeListener<? super K, ? super V> changeListener) {
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.changeListeners = new MapChangeListener[]{changeListener};
            this.changeSize = 1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        public Generic<K, V> addListener(InvalidationListener listener) {
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

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(InvalidationListener listener) {
            if (this.invalidationListeners != null) {
                for (int index = 0; index < this.invalidationSize; index++) {
                    if (listener.equals(this.invalidationListeners[index])) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1) {
                                return new SingleChange(this.changeListeners[0]);
                            }
                            this.invalidationListeners = null;
                            this.invalidationSize = 0;
                            break;
                        }
                        if (this.invalidationSize == 2 && this.changeSize == 0) {
                            return new SingleInvalidation(this.invalidationListeners[1 - index]);
                        }
                        int numMoved = (this.invalidationSize - index) - 1;
                        InvalidationListener[] oldListeners = this.invalidationListeners;
                        if (this.locked) {
                            this.invalidationListeners = new InvalidationListener[this.invalidationListeners.length];
                            System.arraycopy(oldListeners, 0, this.invalidationListeners, 0, index);
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

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> addListener(MapChangeListener<? super K, ? super V> listener) {
            if (this.changeListeners == null) {
                this.changeListeners = new MapChangeListener[]{listener};
                this.changeSize = 1;
            } else {
                int oldCapacity = this.changeListeners.length;
                if (this.locked) {
                    int newCapacity = this.changeSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.changeListeners = (MapChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity);
                } else if (this.changeSize == oldCapacity) {
                    this.changeSize = trim(this.changeSize, this.changeListeners);
                    if (this.changeSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.changeListeners = (MapChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity2);
                    }
                }
                MapChangeListener<? super K, ? super V>[] mapChangeListenerArr = this.changeListeners;
                int i = this.changeSize;
                this.changeSize = i + 1;
                mapChangeListenerArr[i] = listener;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected MapListenerHelper<K, V> removeListener(MapChangeListener<? super K, ? super V> listener) {
            if (this.changeListeners != null) {
                for (int index = 0; index < this.changeSize; index++) {
                    if (listener.equals(this.changeListeners[index])) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1) {
                                return new SingleInvalidation(this.invalidationListeners[0]);
                            }
                            this.changeListeners = null;
                            this.changeSize = 0;
                            break;
                        }
                        if (this.changeSize == 2 && this.invalidationSize == 0) {
                            return new SingleChange(this.changeListeners[1 - index]);
                        }
                        int numMoved = (this.changeSize - index) - 1;
                        MapChangeListener<? super K, ? super V>[] oldListeners = this.changeListeners;
                        if (this.locked) {
                            this.changeListeners = new MapChangeListener[this.changeListeners.length];
                            System.arraycopy(oldListeners, 0, this.changeListeners, 0, index);
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

        @Override // com.brixcore.fakefx.collections.MapListenerHelper
        protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
            InvalidationListener[] curInvalidationList = this.invalidationListeners;
            int curInvalidationSize = this.invalidationSize;
            MapChangeListener<? super K, ? super V>[] curChangeList = this.changeListeners;
            int curChangeSize = this.changeSize;
            try {
                this.locked = true;
                for (int i = 0; i < curInvalidationSize; i++) {
                    try {
                        curInvalidationList[i].invalidated(change.getMap());
                    } catch (Exception e) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    }
                }
                for (int i2 = 0; i2 < curChangeSize; i2++) {
                    try {
                        curChangeList[i2].onChanged(change);
                    } catch (Exception e2) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e2);
                    }
                }
                this.locked = false;
            } catch (Throwable th) {
                this.locked = false;
                throw th;
            }
        }
    }
}
