package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableListValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.NonIterableChange;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.SourceAdapterChange;
import java.util.Arrays;

/* JADX INFO: loaded from: classes6.dex */
public abstract class ListExpressionHelper<E> extends ExpressionHelperBase {
    protected final ObservableListValue<E> observable;

    protected abstract ListExpressionHelper<E> addListener(InvalidationListener invalidationListener);

    protected abstract ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> changeListener);

    protected abstract ListExpressionHelper<E> addListener(ListChangeListener<? super E> listChangeListener);

    protected abstract void fireValueChangedEvent();

    protected abstract void fireValueChangedEvent(ListChangeListener.Change<? extends E> change);

    protected abstract ListExpressionHelper<E> removeListener(InvalidationListener invalidationListener);

    protected abstract ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> changeListener);

    protected abstract ListExpressionHelper<E> removeListener(ListChangeListener<? super E> listChangeListener);

    public static <E> ListExpressionHelper<E> addListener(ListExpressionHelper<E> helper, ObservableListValue<E> observable, InvalidationListener listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        observable.getValue2();
        return helper == null ? new SingleInvalidation(observable, listener) : helper.addListener(listener);
    }

    public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> helper, InvalidationListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <E> ListExpressionHelper<E> addListener(ListExpressionHelper<E> helper, ObservableListValue<E> observable, ChangeListener<? super ObservableList<E>> listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        return helper == null ? new SingleChange(observable, listener) : helper.addListener(listener);
    }

    public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> helper, ChangeListener<? super ObservableList<E>> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <E> ListExpressionHelper<E> addListener(ListExpressionHelper<E> helper, ObservableListValue<E> observable, ListChangeListener<? super E> listener) {
        if (observable == null || listener == null) {
            throw new NullPointerException();
        }
        return helper == null ? new SingleListChange(observable, listener) : helper.addListener(listener);
    }

    public static <E> ListExpressionHelper<E> removeListener(ListExpressionHelper<E> helper, ListChangeListener<? super E> listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (helper == null) {
            return null;
        }
        return helper.removeListener(listener);
    }

    public static <E> void fireValueChangedEvent(ListExpressionHelper<E> helper) {
        if (helper != null) {
            helper.fireValueChangedEvent();
        }
    }

    public static <E> void fireValueChangedEvent(ListExpressionHelper<E> helper, ListChangeListener.Change<? extends E> change) {
        if (helper != null) {
            helper.fireValueChangedEvent(change);
        }
    }

    protected ListExpressionHelper(ObservableListValue<E> observable) {
        this.observable = observable;
    }

    private static class SingleInvalidation<E> extends ListExpressionHelper<E> {
        private final InvalidationListener listener;

        private SingleInvalidation(ObservableListValue<E> observable, InvalidationListener listener) {
            super(observable);
            this.listener = listener;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(InvalidationListener listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(InvalidationListener listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent() {
            this.listener.invalidated(this.observable);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            this.listener.invalidated(this.observable);
        }
    }

    private static class SingleChange<E> extends ListExpressionHelper<E> {
        private ObservableList<E> currentValue;
        private final ChangeListener<? super ObservableList<E>> listener;

        private SingleChange(ObservableListValue<E> observable, ChangeListener<? super ObservableList<E>> listener) {
            super(observable);
            this.listener = listener;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(InvalidationListener listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent() {
            ObservableList<E> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            if (this.currentValue != oldValue) {
                this.listener.changed(this.observable, oldValue, this.currentValue);
            }
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            this.listener.changed(this.observable, this.currentValue, this.currentValue);
        }
    }

    private static class SingleListChange<E> extends ListExpressionHelper<E> {
        private ObservableList<E> currentValue;
        private final ListChangeListener<? super E> listener;

        private SingleListChange(ObservableListValue<E> observable, ListChangeListener<? super E> listener) {
            super(observable);
            this.listener = listener;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(InvalidationListener listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(InvalidationListener listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> listener) {
            return new Generic(this.observable, listener, this.listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> listener) {
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> listener) {
            return new Generic(this.observable, this.listener, listener);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> listener) {
            if (listener.equals(this.listener)) {
                return null;
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent() {
            ObservableList<E> safeOldValue;
            ObservableList<E> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            if (this.currentValue != oldValue) {
                int safeSize = this.currentValue == null ? 0 : this.currentValue.size();
                if (oldValue == null) {
                    safeOldValue = FXCollections.emptyObservableList();
                } else {
                    safeOldValue = FXCollections.unmodifiableObservableList(oldValue);
                }
                this.listener.onChanged(new NonIterableChange.GenericAddRemoveChange(0, safeSize, safeOldValue, this.observable));
            }
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            this.listener.onChanged(new SourceAdapterChange(this.observable, change));
        }
    }

    private static class Generic<E> extends ListExpressionHelper<E> {
        private ChangeListener<? super ObservableList<E>>[] changeListeners;
        private int changeSize;
        private ObservableList<E> currentValue;
        private InvalidationListener[] invalidationListeners;
        private int invalidationSize;
        private ListChangeListener<? super E>[] listChangeListeners;
        private int listChangeSize;
        private boolean locked;

        private Generic(ObservableListValue<E> observable, InvalidationListener listener0, InvalidationListener listener1) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{listener0, listener1};
            this.invalidationSize = 2;
        }

        private Generic(ObservableListValue<E> observable, ChangeListener<? super ObservableList<E>> listener0, ChangeListener<? super ObservableList<E>> listener1) {
            super(observable);
            this.changeListeners = new ChangeListener[]{listener0, listener1};
            this.changeSize = 2;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableListValue<E> observable, ListChangeListener<? super E> listener0, ListChangeListener<? super E> listener1) {
            super(observable);
            this.listChangeListeners = new ListChangeListener[]{listener0, listener1};
            this.listChangeSize = 2;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableListValue<E> observable, InvalidationListener invalidationListener, ChangeListener<? super ObservableList<E>> changeListener) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.changeListeners = new ChangeListener[]{changeListener};
            this.changeSize = 1;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableListValue<E> observable, InvalidationListener invalidationListener, ListChangeListener<? super E> listChangeListener) {
            super(observable);
            this.invalidationListeners = new InvalidationListener[]{invalidationListener};
            this.invalidationSize = 1;
            this.listChangeListeners = new ListChangeListener[]{listChangeListener};
            this.listChangeSize = 1;
            this.currentValue = observable.getValue2();
        }

        private Generic(ObservableListValue<E> observable, ChangeListener<? super ObservableList<E>> changeListener, ListChangeListener<? super E> listChangeListener) {
            super(observable);
            this.changeListeners = new ChangeListener[]{changeListener};
            this.changeSize = 1;
            this.listChangeListeners = new ListChangeListener[]{listChangeListener};
            this.listChangeSize = 1;
            this.currentValue = observable.getValue2();
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(InvalidationListener listener) {
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

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(InvalidationListener listener) {
            if (this.invalidationListeners != null) {
                for (int index = 0; index < this.invalidationSize; index++) {
                    if (listener.equals(this.invalidationListeners[index])) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.listChangeSize == 0) {
                                return new SingleChange(this.observable, this.changeListeners[0]);
                            }
                            if (this.changeSize == 0 && this.listChangeSize == 1) {
                                return new SingleListChange(this.observable, this.listChangeListeners[0]);
                            }
                            this.invalidationListeners = null;
                            this.invalidationSize = 0;
                            break;
                        }
                        if (this.invalidationSize == 2 && this.changeSize == 0 && this.listChangeSize == 0) {
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

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ChangeListener<? super ObservableList<E>> listener) {
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
                ChangeListener<? super ObservableList<E>>[] changeListenerArr = this.changeListeners;
                int i = this.changeSize;
                this.changeSize = i + 1;
                changeListenerArr[i] = listener;
            }
            if (this.changeSize == 1) {
                this.currentValue = this.observable.getValue2();
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ChangeListener<? super ObservableList<E>> listener) {
            if (this.changeListeners != null) {
                for (int index = 0; index < this.changeSize; index++) {
                    if (listener.equals(this.changeListeners[index])) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.listChangeSize == 0) {
                                return new SingleInvalidation(this.observable, this.invalidationListeners[0]);
                            }
                            if (this.invalidationSize == 0 && this.listChangeSize == 1) {
                                return new SingleListChange(this.observable, this.listChangeListeners[0]);
                            }
                            this.changeListeners = null;
                            this.changeSize = 0;
                            break;
                        }
                        if (this.changeSize == 2 && this.invalidationSize == 0 && this.listChangeSize == 0) {
                            return new SingleChange(this.observable, this.changeListeners[1 - index]);
                        }
                        int numMoved = (this.changeSize - index) - 1;
                        ChangeListener<? super ObservableList<E>>[] oldListeners = this.changeListeners;
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

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> addListener(ListChangeListener<? super E> listener) {
            if (this.listChangeListeners == null) {
                this.listChangeListeners = new ListChangeListener[]{listener};
                this.listChangeSize = 1;
            } else {
                int oldCapacity = this.listChangeListeners.length;
                if (this.locked) {
                    int newCapacity = this.listChangeSize < oldCapacity ? oldCapacity : ((oldCapacity * 3) / 2) + 1;
                    this.listChangeListeners = (ListChangeListener[]) Arrays.copyOf(this.listChangeListeners, newCapacity);
                } else if (this.listChangeSize == oldCapacity) {
                    this.listChangeSize = trim(this.listChangeSize, this.listChangeListeners);
                    if (this.listChangeSize == oldCapacity) {
                        int newCapacity2 = ((oldCapacity * 3) / 2) + 1;
                        this.listChangeListeners = (ListChangeListener[]) Arrays.copyOf(this.listChangeListeners, newCapacity2);
                    }
                }
                ListChangeListener<? super E>[] listChangeListenerArr = this.listChangeListeners;
                int i = this.listChangeSize;
                this.listChangeSize = i + 1;
                listChangeListenerArr[i] = listener;
            }
            if (this.listChangeSize == 1) {
                this.currentValue = this.observable.getValue2();
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected ListExpressionHelper<E> removeListener(ListChangeListener<? super E> listener) {
            if (this.listChangeListeners != null) {
                for (int index = 0; index < this.listChangeSize; index++) {
                    if (listener.equals(this.listChangeListeners[index])) {
                        if (this.listChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return new SingleInvalidation(this.observable, this.invalidationListeners[0]);
                            }
                            if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return new SingleChange(this.observable, this.changeListeners[0]);
                            }
                            this.listChangeListeners = null;
                            this.listChangeSize = 0;
                            break;
                        }
                        if (this.listChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return new SingleListChange(this.observable, this.listChangeListeners[1 - index]);
                        }
                        int numMoved = (this.listChangeSize - index) - 1;
                        ListChangeListener<? super E>[] oldListeners = this.listChangeListeners;
                        if (this.locked) {
                            this.listChangeListeners = new ListChangeListener[this.listChangeListeners.length];
                            System.arraycopy(oldListeners, 0, this.listChangeListeners, 0, index + 1);
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.listChangeListeners, index, numMoved);
                        }
                        this.listChangeSize--;
                        if (!this.locked) {
                            this.listChangeListeners[this.listChangeSize] = null;
                            break;
                        }
                        break;
                    }
                }
            }
            return this;
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent() {
            ObservableList<E> safeOldValue;
            if (this.changeSize == 0 && this.listChangeSize == 0) {
                notifyListeners(this.currentValue, null, false);
                return;
            }
            ObservableList<E> oldValue = this.currentValue;
            this.currentValue = this.observable.getValue2();
            if (this.currentValue != oldValue) {
                ListChangeListener.Change<E> change = null;
                if (this.listChangeSize > 0) {
                    int safeSize = this.currentValue == null ? 0 : this.currentValue.size();
                    if (oldValue == null) {
                        safeOldValue = FXCollections.emptyObservableList();
                    } else {
                        safeOldValue = FXCollections.unmodifiableObservableList(oldValue);
                    }
                    change = new NonIterableChange.GenericAddRemoveChange<>(0, safeSize, safeOldValue, this.observable);
                }
                notifyListeners(oldValue, change, false);
                return;
            }
            notifyListeners(this.currentValue, null, true);
        }

        @Override // com.brixcore.fakefx.binding.ListExpressionHelper
        protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
            ListChangeListener.Change<E> mappedChange = this.listChangeSize == 0 ? null : new SourceAdapterChange<>(this.observable, change);
            notifyListeners(this.currentValue, mappedChange, false);
        }

        private void notifyListeners(ObservableList<E> oldValue, ListChangeListener.Change<E> change, boolean noChange) {
            InvalidationListener[] curInvalidationList = this.invalidationListeners;
            int curInvalidationSize = this.invalidationSize;
            ChangeListener<? super ObservableList<E>>[] curChangeList = this.changeListeners;
            int curChangeSize = this.changeSize;
            ListChangeListener<? super E>[] curListChangeList = this.listChangeListeners;
            int curListChangeSize = this.listChangeSize;
            try {
                this.locked = true;
                for (int i = 0; i < curInvalidationSize; i++) {
                    curInvalidationList[i].invalidated(this.observable);
                }
                if (!noChange) {
                    for (int i2 = 0; i2 < curChangeSize; i2++) {
                        curChangeList[i2].changed(this.observable, oldValue, this.currentValue);
                    }
                    if (change != null) {
                        for (int i3 = 0; i3 < curListChangeSize; i3++) {
                            change.reset();
                            curListChangeList[i3].onChanged(change);
                        }
                    }
                }
            } finally {
                this.locked = false;
            }
        }
    }
}
