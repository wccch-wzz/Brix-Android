package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.binding.ExpressionHelperBase;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ListListenerHelper<E> extends ExpressionHelperBase {
    protected abstract ListListenerHelper<E> addListener(InvalidationListener invalidationListener);

    protected abstract ListListenerHelper<E> addListener(ListChangeListener<? super E> listChangeListener);

    protected abstract void fireValueChangedEvent(ListChangeListener.Change<? extends E> change);

    protected abstract ListListenerHelper<E> removeListener(InvalidationListener invalidationListener);

    protected abstract ListListenerHelper<E> removeListener(ListChangeListener<? super E> listChangeListener);

    public static <E> ListListenerHelper<E> addListener(ListListenerHelper<E> helper, InvalidationListener listener) {
        if (listener != null) {
            return helper == null ? new SingleInvalidation(listener) : helper.addListener(listener);
        }
        throw new NullPointerException();
    }

    public static <E> ListListenerHelper<E> removeListener(ListListenerHelper<E> helper, InvalidationListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <E> ListListenerHelper<E> addListener(ListListenerHelper<E> helper, ListChangeListener<? super E> listener) {
        if (listener != null) {
            return helper == null ? new SingleChange(listener) : helper.addListener(listener);
        }
        throw new NullPointerException();
    }

    public static <E> ListListenerHelper<E> removeListener(ListListenerHelper<E> helper, ListChangeListener<? super E> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <E> void fireValueChangedEvent(ListListenerHelper<E> helper, ListChangeListener.Change<? extends E> change) {
        if (helper != null) {
            change.reset();
            helper.fireValueChangedEvent(change);
        }
    }

    public static <E> boolean hasListeners(ListListenerHelper<E> helper) {
        return helper != null;
    }

    private static class SingleInvalidation<E> extends ListListenerHelper<E> {
        private final InvalidationListener listener;

        private SingleInvalidation(InvalidationListener listener) {
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> addListener(InvalidationListener listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(InvalidationListener listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> addListener(ListChangeListener<? super E> listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            try {
                this.listener.invalidated(change.getList());
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    private static class SingleChange<E> extends ListListenerHelper<E> {
        private final ListChangeListener<? super E> listener;

        private SingleChange(ListChangeListener<? super E> listener) {
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> addListener(InvalidationListener listener) {
            return new Generic(listener, this.listener);
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> addListener(ListChangeListener<? super E> listener) {
            return new Generic(this.listener, listener);
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            try {
                this.listener.onChanged(change);
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    private static class Generic<E> extends ListListenerHelper<E> {
        private ListChangeListener<? super E>[] changeListeners;
        private int changeSize;
        private InvalidationListener[] invalidationListeners;
        private int invalidationSize;
        private boolean locked;

        private Generic(InvalidationListener listener0, InvalidationListener listener1) {
            this.invalidationListeners = new InvalidationListener[]{listener0, listener1};
            this.invalidationSize = 2;
        }

        private Generic(ListChangeListener<? super E> listener0, ListChangeListener<? super E> listener1) {
            this.changeListeners = new ListChangeListener[]{listener0, listener1};
            this.changeSize = 2;
        }

        private Generic(InvalidationListener invalidationListener, ListChangeListener<? super E> changeListener) {
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.changeListeners = new ListChangeListener[]{changeListener};
            this.changeSize = 1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        public Generic<E> addListener(InvalidationListener listener) {
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

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(InvalidationListener listener) {
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

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> addListener(ListChangeListener<? super E> listener) {
            if (this.changeListeners == null) {
                this.changeListeners = new ListChangeListener[]{listener};
                this.changeSize = 1;
            } else {
                int oldCapacity = this.changeListeners.length;
                if (this.locked) {
                    int newCapacity = this.changeSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.changeListeners = (ListChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity);
                } else if (this.changeSize == oldCapacity) {
                    this.changeSize = trim(this.changeSize, this.changeListeners);
                    if (this.changeSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.changeListeners = (ListChangeListener[]) Arrays.copyOf(this.changeListeners, newCapacity2);
                    }
                }
                ListChangeListener<? super E>[] listChangeListenerArr = this.changeListeners;
                int i = this.changeSize;
                this.changeSize = i + 1;
                listChangeListenerArr[i] = listener;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected ListListenerHelper<E> removeListener(ListChangeListener<? super E> listener) {
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
                        ListChangeListener<? super E>[] oldListeners = this.changeListeners;
                        if (this.locked) {
                            this.changeListeners = new ListChangeListener[this.changeListeners.length];
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

        @Override // com.brixcore.fakefx.collections.ListListenerHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            InvalidationListener[] curInvalidationList = this.invalidationListeners;
            int curInvalidationSize = this.invalidationSize;
            ListChangeListener<? super E>[] curChangeList = this.changeListeners;
            int curChangeSize = this.changeSize;
            try {
                this.locked = true;
                for (int i = 0; i < curInvalidationSize; i++) {
                    try {
                        curInvalidationList[i].invalidated(change.getList());
                    } catch (Exception e) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    }
                }
                for (int i2 = 0; i2 < curChangeSize; i2++) {
                    change.reset();
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
