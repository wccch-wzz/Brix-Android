package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty;
import com.brixcore.fakefx.beans.value.ObservableSetValue;
import com.brixcore.fakefx.binding.StringFormatter;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes16.dex */
public abstract class SetExpression<E> implements ObservableSetValue<E> {
    private static final ObservableSet EMPTY_SET = new EmptyObservableSet();

    public abstract ReadOnlyBooleanProperty emptyProperty();

    public abstract ReadOnlyIntegerProperty sizeProperty();

    private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
        private static final Iterator iterator = new Iterator() { // from class: com.brixcore.fakefx.beans.binding.SetExpression.EmptyObservableSet.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return false;
            }

            @Override // java.util.Iterator
            public Object next() {
                throw new NoSuchElementException();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        private EmptyObservableSet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<E> iterator() {
            return iterator;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return 0;
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void addListener(SetChangeListener<? super E> setChangeListener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void removeListener(SetChangeListener<? super E> setChangeListener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
        }
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public ObservableSet<E> getValue2() {
        return get();
    }

    public static <E> SetExpression<E> setExpression(final ObservableSetValue<E> value) {
        if (value != null) {
            return value instanceof SetExpression ? (SetExpression) value : new SetBinding<E>() { // from class: com.brixcore.fakefx.beans.binding.SetExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.SetBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.SetBinding
                protected ObservableSet<E> computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.SetBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Set must be specified.");
    }

    public int getSize() {
        return size();
    }

    public BooleanBinding isEqualTo(ObservableSet<?> other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(ObservableSet<?> other) {
        return Bindings.notEqual(this, other);
    }

    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    @Override // java.util.Set, java.util.Collection
    public int size() {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.size() : set.size();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean isEmpty() {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.isEmpty() : set.isEmpty();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean contains(Object obj) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.contains(obj) : set.contains(obj);
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.iterator() : set.iterator();
    }

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray() {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.toArray() : set.toArray();
    }

    @Override // java.util.Set, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        ObservableSet<E> observableSet = get();
        return observableSet == null ? (T[]) EMPTY_SET.toArray(tArr) : (T[]) observableSet.toArray(tArr);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean add(E element) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.add(element) : set.add(element);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean remove(Object obj) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.remove(obj) : set.remove(obj);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean containsAll(Collection<?> objects) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.contains(objects) : set.containsAll(objects);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean addAll(Collection<? extends E> elements) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.addAll(elements) : set.addAll(elements);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean removeAll(Collection<?> objects) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.removeAll(objects) : set.removeAll(objects);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean retainAll(Collection<?> objects) {
        ObservableSet<E> set = get();
        return set == null ? EMPTY_SET.retainAll(objects) : set.retainAll(objects);
    }

    @Override // java.util.Set, java.util.Collection
    public void clear() {
        ObservableSet<E> set = get();
        if (set == null) {
            EMPTY_SET.clear();
        } else {
            set.clear();
        }
    }
}
